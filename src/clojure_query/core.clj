(ns clojure-query.core
  (:require [clojure-query.search :refer [search top-results]]
            [clojure.java.browse :refer [browse-url]])  
  (:gen-class))


(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (if-not (nil? args)
          (let [[search-engine query] args
                search-results (future (search (keyword search-engine) query))]
              (println (first @search-results)))))
  