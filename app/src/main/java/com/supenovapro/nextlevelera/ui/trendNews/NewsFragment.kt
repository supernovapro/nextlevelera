package com.supenovapro.nextlevelera.ui.trendNews

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.supenovapro.nextlevelera.R
import com.supenovapro.nextlevelera.data.ClimateNews
import com.supenovapro.nextlevelera.data.NewsWebsite
import com.supenovapro.nextlevelera.data.TrendNews
import com.supenovapro.nextlevelera.databinding.FragmentNewsArticlesBinding
import com.supenovapro.nextlevelera.ui.bookmark.BookmarkFragment
import com.supenovapro.nextlevelera.ui.browser.News
import com.supenovapro.nextlevelera.ui.details.DetailsAdapter
import com.supenovapro.nextlevelera.ui.details.DetailsFragment
import com.supenovapro.nextlevelera.ui.settings.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NewsFragment : Fragment(R.layout.fragment_news_articles),
    NewsChannelsAdapter.OnItemClickListener,
    TrendNewsAdapter.OnItemClickListener,
    DetailsAdapter.OnItemClickListener {

    private val trendViewModel: NewsViewModel by viewModels()
    private var _binding: FragmentNewsArticlesBinding? = null
    private var bookmarkSize = 0
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNewsArticlesBinding.bind(view)
        val website = newsWebsites()
        val pagerChannelsAdapter = NewsChannelsAdapter(this)
        val climateAdapter = DetailsAdapter(this)
        val adapter = TrendNewsAdapter(this)

        binding.apply {
            val toolbar = mainActToolbar
            toolbar.inflateMenu(R.menu.top_menu)
            setTopMenu(toolbar)
            artFragmentRecyclerview.setHasFixedSize(true)
            artFragmentRecyclerview.adapter = adapter
            artFraClimateRecyclerview.setHasFixedSize(true)
            artFraClimateRecyclerview.adapter = climateAdapter
            artFraClimateRecyclerview.layoutManager = GridLayoutManager(view.context, 1,
                GridLayoutManager.HORIZONTAL, false)
            pagerChannelsAdapter.submitList(newsWebsites())
            artFragmentPager2Content.adapter = pagerChannelsAdapter
            artFragmentPager2Content.registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    try {
                        artPager2ContentName.text = newsWebsites()[position].name
                        currentNewsCategory(position)
                    } catch (exception: Exception) {
                        exception.fillInStackTrace()
                    }
                    super.onPageSelected(position)
                }
            })


            artFragToClimateFrag.setOnClickListener {
                val detailsFragment = DetailsFragment()
                val fragmentManager = parentFragmentManager
                val fragmentTransaction = fragmentManager.beginTransaction()
                fragmentTransaction.replace(
                    R.id.nav_main_fragments_host,
                    detailsFragment,
                    "DetailsFragment"
                )
                fragmentTransaction.addToBackStack("DetailsFragment")
                fragmentTransaction.commit()

            }

        }

        trendViewModel.trendBookmark.observe(viewLifecycleOwner){bookmark ->
            bookmarkSize = bookmark.size
        }

        trendViewModel.trendNews.observe(viewLifecycleOwner) { result ->
            adapter.submitList(result.data)
        }
        trendViewModel.climateNews.observe(viewLifecycleOwner) { climate ->
            if (climate.data?.size!! > 10) {
                climateAdapter.submitList(climate.data.subList(0, 3))
            } else {
                climateAdapter.submitList(climate.data)
            }
        }

    }

    private fun currentNewsCategory(position: Int) {
        val textSizes = listOf(
            if (position in 1..12) 20f else 13f,
            if (position in 13..16) 20f else 13f,
            if (position in 17..22) 20f else 13f,
            if (position in 23..26) 20f else 13f,
            if (position in 27..30) 20f else 13f
        )
        binding.apply {
            worldNewsTextViewFrag.textSize = textSizes[0]
            cultureNewsTextViewFrag.textSize = textSizes[1]
            sportNewsTextViewFrag.textSize = textSizes[2]
            financeNewsTextViewFrag.textSize = textSizes[3]
            politicsNewsTextViewFrag.textSize = textSizes[4]

        }
    }

    override fun onClimateClick(climateNews: ClimateNews) {
        val intent = Intent(context, News::class.java)
        intent.putExtra("url", climateNews.url)
        startActivity(intent)
    }

    override fun onClimateBookmarkClick(climateNews: ClimateNews) {
        trendViewModel.bookmarkClimateNews(climateNews)
    }

    override fun onClimateTwitClick(climate: ClimateNews) {
        TODO("Not yet implemented")
    }

    override fun onClimateShareClick(climate: ClimateNews) {
        TODO("Not yet implemented")
    }

    override fun onChannelClick(channel: NewsWebsite) {
        val intent = Intent(context, News::class.java)
        intent.putExtra("url", channel.websiteLink)
        startActivity(intent)
    }

    override fun onArticleClick(article: TrendNews) {
       // val intent = Intent(context, News::class.java)
       // intent.putExtra("url", article.url)
      //  startActivity(intent)
    }

    override fun onBookmarkClick(article: TrendNews) {
        trendViewModel.bookmarkNewsArticle(article)
    }

    private fun setTopMenu(toolbar: MaterialToolbar) {
        toolbar.setOnMenuItemClickListener { topItem ->
            when (topItem.itemId) {
                R.id.top_menu_settings -> {
                    val settingFrag = SettingsFragment()
                    val fragmentManager = parentFragmentManager
                    val fragmentTransaction = fragmentManager.beginTransaction()
                    fragmentTransaction.replace(
                        R.id.nav_main_fragments_host,
                        settingFrag,
                        "SettingsFragment"
                    )
                    fragmentTransaction.addToBackStack("SettingsFragment")
                    fragmentTransaction.commit()
                    true
                }

                R.id.top_menu_bookmark -> {
                    if (bookmarkSize > 0) {
                        val bookmarkFrag = BookmarkFragment()
                        val fragmentManager = parentFragmentManager
                        val fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(
                            R.id.nav_main_fragments_host,
                            bookmarkFrag,
                            "BookmarkFragment"
                        )
                        fragmentTransaction.addToBackStack("BookmarkFragment")
                        fragmentTransaction.commit()
                    }else{
                        Snackbar.make(binding.root, "Bookmark List Empty", Snackbar.LENGTH_LONG)
                            .show()
                    }
                    true
                }

                R.id.top_menu_more -> {
                    Snackbar.make(binding.root, "top more ", Snackbar.LENGTH_LONG).show()
                    true
                }

                R.id.top_menu_privacy -> {
                    Snackbar.make(binding.root, "top privacy", Snackbar.LENGTH_LONG).show()
                    true
                }

                R.id.top_menu_share -> {
                    Snackbar.make(binding.root, "top share ", Snackbar.LENGTH_LONG).show()
                    true
                }

                else -> {
                    return@setOnMenuItemClickListener true
                }
            }
        }
    }

    //send data back main activity
    interface TrendNewsFragmentListener {}

    private var trendNewsListener: TrendNewsFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        trendNewsListener = if (context is TrendNewsFragmentListener) {
            context
        } else {
            throw RuntimeException(context.toString() + "must impl BOOKMARK listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        trendNewsListener = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun newsWebsites(): ArrayList<NewsWebsite> {
        val newsWebsites = ArrayList<NewsWebsite>()
        //Word NEWS
        newsWebsites.add(
            NewsWebsite(
                R.drawable.bbc_channel,
                "BBC News",
                "https://www.bbc.com/news",
                "Comprehensive news coverage from around the world."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.china_daily_channel,
                "China Daily",
                "https://www.chinadaily.com.cn",
                "Largest English-language newspaper in China, providing insights into Chinese politics, economy, and culture."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.hundu_channel,
                "The Hindu",
                "https://www.thehindu.com",
                "English-language newspaper based in India, providing news coverage on national and international events."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.fox_channel,
                "Fox News",
                "https://www.foxnews.com",
                "Prominent American news network known for conservative-leaning coverage and commentary."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.france24_channel,
                "France 24",
                "https://www.france24.com",
                "French news channel covering international news from a French perspective."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.trt_channel,
                "TRT World",
                "https://www.trtworld.com",
                "Turkish international news channel covering global news and current affairs."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.euronews_channel,
                "Euronews",
                "https://www.euronews.com",
                "Multilingual news media service headquartered in France, providing news from a European perspective."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.jazeera_channel,
                "Al Jazeera",
                "https://www.aljazeera.com",
                "Qatari-based news network offering global news, investigative journalism, and in-depth analysis."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.guardian_channel,
                "The Guardian",
                "https://www.theguardian.com",
                "British publication known for its investigative journalism, in-depth features, and opinion pieces."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.reuters_channel,
                "Reuters",
                "https://www.reuters.com",
                "Leading international news agency providing coverage of global events, business, and finance."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.smh_channel,
                "The Sydney Morning Herald",
                "https://www.smh.com.au",
                "Australia's leading newspaper covering local, national, and international news."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.dw_channel,
                "DW (Deutsche Welle)",
                "https://www.dw.com",
                "German international broadcaster offering news and information in multiple languages."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.south_china_channel,
                "South China Morning Post",
                "https://www.scmp.com",
                "Leading English-language newspaper based in Hong Kong, covering news and events in the Asia-Pacific region."
            )
        )
        // USA
        newsWebsites.add(
            NewsWebsite(
                R.drawable.nbc_channel,
                "NBC News",
                "https://www.nbcnews.com",
                "American news network providing coverage of current events, politics, and popular culture."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.huff_channel,
                "The Huffington Post",
                "https://www.huffpost.com",
                "American digital media outlet offering news, analysis, and commentary on various topics."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.nyt_channel,
                "The New York Times",
                "https://www.nytimes.com",
                "Renowned American newspaper covering national and international news, politics, culture, and more."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.cnn_channel,
                "CNN",
                "https://www.cnn.com",
                "Global news outlet delivering breaking news, politics, business, entertainment, and more."
            )
        )
        // SPORT NEWS
        newsWebsites.add(
            NewsWebsite(
                R.drawable.fox_sport_channel,
                "Fox Sports",
                "https://www.foxsports.com",
                "US sports network offering news, analysis, live scores, and exclusive videos."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.goal_channel,
                "Goal.com",
                "https://www.goal.com",
                "International football (soccer) news and coverage, featuring articles, videos, and statistics."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.bein_channel,
                "beIN SPORTS",
                "https://www.beinsports.com",
                "Sports network offering live sports coverage, news, highlights, and analysis."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.marca_channel,
                "Marca",
                "https://www.marca.com",
                "Spanish sports newspaper providing news, analysis, and coverage of various sports."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.espn_channel,
                "ESPN",
                "https://www.espn.com",
                "US-based sports network providing comprehensive sports news, analysis, and live streaming."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.br_channel,
                "BR",
                "https://bleacherreport.com",
                "US sports media company offering sports news, analysis, and viral sports content."
            )
        )
        //Finance news
        newsWebsites.add(
            NewsWebsite(
                R.drawable.forbes_channel,
                "Forbes",
                "https://www.forbes.com",
                "Renowned source for business, entrepreneurship, and lifestyle news, featuring lists and rankings."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.wall_street_channel,
                "The Wall Street Journal",
                "https://www.wsj.com",
                "Business-focused newspaper providing financial news, analysis, and in-depth reporting."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.bloomberg_channel,
                "Bloomberg",
                "https://www.bloomberg.com",
                "Global business and financial news outlet covering markets, technology, politics, and more."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.ap_channel,
                "Associated Press (AP)",
                "https://apnews.com",
                "Renowned American news agency providing comprehensive coverage of national and international events."
            )
        )
        //Politics news
        newsWebsites.add(
            NewsWebsite(
                R.drawable.washin_channel,
                "The Washington Post",
                "https://www.washingtonpost.com",
                "Influential American newspaper covering politics, national news, and world affairs."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.independent_channel,
                "The Independent",
                "https://www.independent.co.uk",
                "British newspaper covering global events, politics, culture, and lifestyle."
            )
        )
        newsWebsites.add(
            NewsWebsite(
                R.drawable.le_monde_channel,
                "Le Monde",
                "https://www.lemonde.fr",
                "French daily newspaper offering comprehensive coverage of news, politics, and culture."
            )
        )

        return newsWebsites
    }

}