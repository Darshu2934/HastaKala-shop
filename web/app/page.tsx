import { AiSuggester } from "@/components/AiSuggester";

export default function Home() {
  return (
    <main className="mx-auto flex min-h-screen max-w-3xl flex-col px-4 py-10 sm:px-6">
      <header className="mb-10 text-center">
        <p className="text-xs font-semibold uppercase tracking-[0.35em] text-amber-800/80">
          Hasta Kala
        </p>
        <h1 className="mt-2 font-display text-3xl font-bold text-amber-950 sm:text-4xl">
          Artisan AI assistant
        </h1>
        <p className="mt-3 text-sm text-amber-900/80 sm:text-base">
          Describe your craft in a few words; get a heritage-aware product
          narrative and SEO tags you can paste into listings.
        </p>
      </header>
      <AiSuggester />
    </main>
  );
}
