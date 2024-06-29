
package com.example.animeflix

import com.lagradost.cloudstream3.MainAPI
import com.lagradost.cloudstream3.TvType
import com.lagradost.cloudstream3.utils.AppUtils.toJson
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.loadExtractor
import org.jsoup.Jsoup

class AnimeFlixProvider : MainAPI() {
    override var mainUrl = "https://animeflix.gg"
    override var name = "AnimeFlix"
    override var lang = "en"
    override val hasMainPage = true

    override val supportedTypes = setOf(TvType.Anime)

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "\$mainUrl/search?query=\$query"
        val document = Jsoup.connect(url).get()
        val results = mutableListOf<SearchResponse>()
        
        document.select(".search-result-item").forEach {
            val title = it.selectFirst(".title")?.text() ?: return@forEach
            val href = it.selectFirst("a")?.attr("href") ?: return@forEach
            val posterUrl = it.selectFirst("img")?.attr("src")
            results.add(TvSeriesSearchResponse(
                title,
                href,
                this.name,
                TvType.Anime,
                posterUrl = posterUrl
            ))
        }
        return results
    }

    override suspend fun load(url: String): LoadResponse {
        val document = Jsoup.connect(url).get()
        val title = document.selectFirst(".anime-title")?.text() ?: throw ErrorLoadingException()
        val posterUrl = document.selectFirst(".anime-poster img")?.attr("src")
        
        val episodes = mutableListOf<Episode>()
        document.select(".episode-list a").forEach {
            val episodeTitle = it.text()
            val episodeUrl = it.attr("href")
            episodes.add(Episode(episodeUrl, episodeTitle))
        }

        return TvSeriesLoadResponse(
            title,
            url,
            this.name,
            TvType.Anime,
            episodes = episodes,
            posterUrl = posterUrl
        )
    }

    override suspend fun loadLinks(url: String, referer: String): List<ExtractorLink> {
        val document = Jsoup.connect(url).get()
        val videoUrl = document.selectFirst("video source")?.attr("src") ?: throw ErrorLoadingException()
        
        return listOf(ExtractorLink(
            name = "AnimeFlix",
            source = videoUrl,
            url = videoUrl,
            referer = referer
        ))
    }
}
