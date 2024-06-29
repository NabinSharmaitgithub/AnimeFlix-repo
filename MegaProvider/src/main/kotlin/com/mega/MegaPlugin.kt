package com.mega

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Crunchyroll : Plugin {
    override val name: String = "Crunchyroll"

    override fun search(query: String): List<MediaItem> {
        val url = "https://www.crunchyroll.com/search?q=$query"
        val document = Jsoup.connect(url).get()
        return parseResults(document)
    }

    private fun parseResults(document: Document): List<MediaItem> {
        val results = document.select(".result-item")
        return results.map {
            MediaItem(
                title = it.select(".result-title").text(),
                url = "https://www.crunchyroll.com" + it.select("a").attr("href"),
                image = it.select("img").attr("src"),
                description = it.select(".result-description").text()
            )
        }
    }
}
