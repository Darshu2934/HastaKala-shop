import OpenAI from "openai";
import { NextResponse } from "next/server";
import type { SuggestResponse } from "@/lib/suggest-types";

export const runtime = "nodejs";

const SYSTEM_PROMPT = `You are a writing partner for Indian handicraft artisans selling on marketplaces and their own shops.
Your tone is warm, respectful, and specific to regional craft vocabulary when appropriate (weaving, pottery, metalwork, wood, terracotta, embroidery, etc.).
Avoid clichés and empty superlatives. Ground details in the title and material the user gives.
Always respond with a single JSON object only, no markdown, matching this shape:
{"productDescription": string (2-4 short paragraphs, plain text with \\n\\n between paragraphs), "seoTitle": string (<=60 chars), "seoMetaDescription": string (<=155 chars), "seoTags": string[] (8-14 concise tags, lowercase, comma-separated concepts as array items)}`;

export async function POST(request: Request) {
  const apiKey = process.env.OPENAI_API_KEY;
  if (!apiKey) {
    return NextResponse.json(
      { error: "Missing OPENAI_API_KEY on the server." },
      { status: 500 }
    );
  }

  let body: unknown;
  try {
    body = await request.json();
  } catch {
    return NextResponse.json({ error: "Invalid JSON body." }, { status: 400 });
  }

  const title =
    typeof body === "object" &&
    body !== null &&
    "title" in body &&
    typeof (body as { title: unknown }).title === "string"
      ? (body as { title: string }).title.trim()
      : "";
  const material =
    typeof body === "object" &&
    body !== null &&
    "material" in body &&
    typeof (body as { material: unknown }).material === "string"
      ? (body as { material: string }).material.trim()
      : "";

  if (!title || !material) {
    return NextResponse.json(
      { error: "Both 'title' and 'material' are required strings." },
      { status: 400 }
    );
  }

  const model = process.env.OPENAI_MODEL ?? "gpt-4o-mini";

  const openai = new OpenAI({ apiKey });

  try {
    const completion = await openai.chat.completions.create({
      model,
      temperature: 0.7,
      response_format: { type: "json_object" },
      messages: [
        { role: "system", content: SYSTEM_PROMPT },
        {
          role: "user",
          content: `Product title: ${title}\nPrimary material / medium: ${material}`,
        },
      ],
    });

    const raw = completion.choices[0]?.message?.content;
    if (!raw) {
      return NextResponse.json(
        { error: "No content returned from the model." },
        { status: 502 }
      );
    }

    let parsed: SuggestResponse;
    try {
      parsed = JSON.parse(raw) as SuggestResponse;
    } catch {
      return NextResponse.json(
        { error: "Model returned invalid JSON." },
        { status: 502 }
      );
    }

    if (
      typeof parsed.productDescription !== "string" ||
      typeof parsed.seoTitle !== "string" ||
      typeof parsed.seoMetaDescription !== "string" ||
      !Array.isArray(parsed.seoTags) ||
      !parsed.seoTags.every((t) => typeof t === "string")
    ) {
      return NextResponse.json(
        { error: "Model JSON did not match the expected schema." },
        { status: 502 }
      );
    }

    return NextResponse.json(parsed satisfies SuggestResponse);
  } catch (err) {
    const message = err instanceof Error ? err.message : "OpenAI request failed.";
    return NextResponse.json({ error: message }, { status: 502 });
  }
}
