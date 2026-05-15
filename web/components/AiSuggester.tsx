"use client";

import { useState } from "react";
import type { SuggestResponse } from "@/lib/suggest-types";

type ApiError = { error?: string };

export function AiSuggester() {
  const [title, setTitle] = useState("");
  const [material, setMaterial] = useState("");
  const [result, setResult] = useState<SuggestResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setResult(null);
    setLoading(true);
    try {
      const res = await fetch("/api/suggest", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ title: title.trim(), material: material.trim() }),
      });
      const data = (await res.json()) as SuggestResponse & ApiError;
      if (!res.ok) {
        setError(data.error ?? "Something went wrong.");
        return;
      }
      if ("error" in data && data.error) {
        setError(data.error);
        return;
      }
      setResult(data);
    } catch {
      setError("Network error. Is the dev server running?");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="space-y-8">
      <form
        onSubmit={handleSubmit}
        className="rounded-2xl border border-amber-200/80 bg-gradient-to-b from-amber-50/90 to-stone-100/90 p-6 shadow-lg shadow-amber-900/10 ring-1 ring-amber-100/60 backdrop-blur-sm sm:p-8"
      >
        <div className="space-y-5">
          <div>
            <label
              htmlFor="title"
              className="block text-sm font-semibold text-amber-950"
            >
              Product title
            </label>
            <input
              id="title"
              name="title"
              required
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              placeholder="e.g. Madhubani hand-painted wall plate"
              className="mt-1.5 w-full rounded-xl border border-amber-300/70 bg-parchment px-3 py-2.5 text-amber-950 shadow-inner shadow-amber-900/5 outline-none ring-0 transition placeholder:text-amber-800/40 focus:border-amber-600 focus:ring-2 focus:ring-amber-400/50"
            />
          </div>
          <div>
            <label
              htmlFor="material"
              className="block text-sm font-semibold text-amber-950"
            >
              Material / craft medium
            </label>
            <input
              id="material"
              name="material"
              required
              value={material}
              onChange={(e) => setMaterial(e.target.value)}
              placeholder="e.g. Terracotta, natural pigments"
              className="mt-1.5 w-full rounded-xl border border-amber-300/70 bg-parchment px-3 py-2.5 text-amber-950 shadow-inner shadow-amber-900/5 outline-none transition placeholder:text-amber-800/40 focus:border-amber-600 focus:ring-2 focus:ring-amber-400/50"
            />
          </div>
        </div>
        <button
          type="submit"
          disabled={loading}
          className="mt-6 w-full rounded-xl bg-gradient-to-r from-amber-800 to-amber-950 px-4 py-3 text-sm font-semibold text-amber-50 shadow-md shadow-amber-950/25 transition hover:from-amber-900 hover:to-stone-900 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {loading ? "Weaving suggestions…" : "Get AI suggestions"}
        </button>
      </form>

      <section
        aria-live="polite"
        className="min-h-[120px] rounded-2xl border border-stone-200/90 bg-stone-50/90 p-6 shadow-inner shadow-amber-900/5 sm:p-8"
      >
        <h2 className="font-display text-lg font-bold text-amber-950">
          Suggestions
        </h2>
        {error && (
          <p className="mt-3 rounded-lg border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-900">
            {error}
          </p>
        )}
        {!error && !result && !loading && (
          <p className="mt-3 text-sm text-amber-900/70">
            Your culturally grounded description and SEO fields will appear here.
          </p>
        )}
        {loading && (
          <p className="mt-3 animate-pulse text-sm text-amber-900/70">
            Calling the model…
          </p>
        )}
        {result && (
          <div className="mt-4 space-y-6 text-amber-950">
            <div>
              <h3 className="text-xs font-bold uppercase tracking-wide text-amber-800/90">
                Product description
              </h3>
              <div className="mt-2 whitespace-pre-line text-sm leading-relaxed text-amber-950/95">
                {result.productDescription}
              </div>
            </div>
            <div className="grid gap-4 sm:grid-cols-2">
              <div>
                <h3 className="text-xs font-bold uppercase tracking-wide text-amber-800/90">
                  SEO title
                </h3>
                <p className="mt-1 rounded-lg border border-amber-100 bg-amber-50/80 px-3 py-2 text-sm">
                  {result.seoTitle}
                </p>
              </div>
              <div className="sm:col-span-2">
                <h3 className="text-xs font-bold uppercase tracking-wide text-amber-800/90">
                  Meta description
                </h3>
                <p className="mt-1 rounded-lg border border-amber-100 bg-amber-50/80 px-3 py-2 text-sm">
                  {result.seoMetaDescription}
                </p>
              </div>
            </div>
            <div>
              <h3 className="text-xs font-bold uppercase tracking-wide text-amber-800/90">
                SEO tags
              </h3>
              <ul className="mt-2 flex flex-wrap gap-2">
                {result.seoTags.map((tag, i) => (
                  <li
                    key={`${tag}-${i}`}
                    className="rounded-full border border-amber-300/60 bg-amber-100/70 px-3 py-1 text-xs font-medium text-amber-950"
                  >
                    {tag}
                  </li>
                ))}
              </ul>
            </div>
          </div>
        )}
      </section>
    </div>
  );
}
