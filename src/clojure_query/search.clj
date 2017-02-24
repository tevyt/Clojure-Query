(ns clojure-query.search
    (:require [clj-http.client :as client]
              [clojure.data.json :as json]))

;;Identifies application with Google api
(def google-custom-search-api-key (System/getenv "GOOGLE_SEARCH_API_KEY"))
;;The ID for the custom search engine that performs the search
(def google-custom-search-engine-id (System/getenv "GOOGLE_CUSTOM_SEARCH_ENGINE_ID"))
;;Base url for Google searches
(def google-custom-search-api-url "https://www.googleapis.com/customsearch/v1")

;;Bing Search API key.
(def bing-search-api-key (System/getenv "BING_SEARCH_API_KEY"))
;;Base url for Bing searches.
(def bing-search-api-url "https://api.cognitive.microsoft.com/bing/v5.0/search")

(defn google-query
    "Sends query to Google"
    [query]
    (client/get google-custom-search-api-url {:query-params 
                                               {"q" query, 
                                                "key" google-custom-search-api-key,
                                                "cx" google-custom-search-engine-id}}))
(defn bing-query
    "Sends query to Bing"
    [query-string]
    (client/get bing-search-api-url {:query-params {"q" query-string,
                                                    "count" 5},
                                     :headers {"Ocp-Apim-Subscription-Key" bing-search-api-key}}))

(def search-functions {:google google-query,
                       :bing bing-query})
(defn results
    "Get the items from a google search result"
    [response-body results-key]
    (-> response-body
        json/read-str
        (get-in results-key)))

(defn search
    "Send a query with the provided search engine"
    [search-engine query-string]
    (let [query (search-engine search-functions)
          results-key (if (= search-engine :bing) ["webPages" "value"] ["items"])
          url-key     (if (= search-engine :bing) "url" "link")]
      (map #(get % url-key)
            (-> query-string
                query
                :body
                (results results-key)))))

(defn top-results
    [query-string]
    (let [bing-results   (future (search :bing query-string))
          google-results (future (search :google query-string))]
      {:bing   (first @bing-results),
       :google (first @google-results)}))