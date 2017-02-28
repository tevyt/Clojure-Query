(ns clojure-query.core
  (:require [clojure-query.search :refer [search top-results]]
            [clojure.java.browse :refer [browse-url]])  
  (:gen-class))

(defn inline-print
  [& more]
  (apply print more)
  (flush))

(defn prompt-user
  ([] 
   (println "Choose a search engine: ")
   (println "1. Google [default]")
   (println "2. Bing")
   (inline-print "=> ")
   (let [search-engine (read-line)]
     (cond (or (empty? search-engine) (= "1" search-engine)) (prompt-user :google)
           (= "2" search-engine) (prompt-user :bing)
           :else (do (println "Invalid Selection") (prompt-user)))))
  ([search-engine] 
   (if (= search-engine :google)
     (println "Using Google")
     (println "Using Bing"))
   (inline-print "Enter a query: ")
   (let [query (read-line)
         complete-message (promise)
         search-results (do (println "Searching for " query) 
                            (future (search search-engine query)))]
     (deliver complete-message (println "Top result: " (first @search-results)))
     (browse-url (first @search-results))
     (inline-print "Search again? (Y/N)[N]: ")
     (let [search-again (clojure.string/upper-case (read-line))]
       (if (= "Y" search-again)
         (prompt-user))))))



(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (if-not (nil? args)
    (let [[search-engine query] args
          search-results (future (search (keyword search-engine) query))]
      (browse-url (first @search-results)))
    (prompt-user)))
