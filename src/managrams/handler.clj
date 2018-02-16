(ns managrams.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [clojure.string :as str]
            [clojure.core.reducers :as r]
            [managrams.primes :as p]
            [managrams.letters :as l]))

(defn strip-ext [word]
  (first (str/split word #"\.")))

(defn matches-for-length [word]
  (if (> (count word) 10)
    (l/match-words word)
    (p/prime-match-words word)))

(defn anagrams-for [gross-word limit]
  (let [word (strip-ext gross-word)
        matching-words (matches-for-length word)]
    (if (= limit -1)
      matching-words
      (take limit (into [] matching-words)))))

(defn process-new-words [words]
  (let [big-uns (filter #(> (count %) 10) words)
        small-fries (filter #(<= (count %) 10) words)]
    (do 
      (l/update-all-words big-uns)
      (p/update-all-prime-words small-fries))))

(defn delete-all-words []
  (do
    (l/reset-big-words)
    (p/reset-small-words)))

(defn delete-word [gross-word]
  (let [word (strip-ext gross-word)]
    (if (> (count word) 10)
      (l/delete-big-word word)
      (p/delete-small-word word))))

(defapi app
  (POST "/words.json" []
    :body-params [words :- [String]]
    {:status 201 :body {:words (process-new-words words)}})

  (DELETE "/words/:word" [word]
    {:status 204 :body {:deleted (delete-word word)}})

  (DELETE "/words.json" []
    {:status 204 :body {:deleted (delete-all-words)}})

  (context "/anagrams" []
    (GET "/:word" [word]
      :query-params [{limit :- Long -1}]
      :return {:anagrams [String]}
      (ok {:anagrams (anagrams-for word limit)}))))
