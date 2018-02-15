(ns managrams.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [clojure.string :as str]
            [clojure.core.reducers :as r]
            [managrams.primes :as p]))

(def all-words (atom {}))

(defn word->key [word]
  (str/join (sort word)))

(defn match-words [word]
  (remove #(= word %) (@all-words (word->key word))))

(defn strip-ext [word]
  (first (str/split word #"\.")))

(defn matches-for-length [word]
  (if (> (count word) 10)
    (match-words word)
    (p/prime-match-words word)))

(defn anagrams-for [gross-word limit]
  (let [word (strip-ext gross-word)
        matching-words (matches-for-length word)]
    (if (= limit -1)
      matching-words
      (take limit matching-words))))

(defn word-map-reducer
  ([] {})
  ([coll word]
    (let [key (word->key word)]
      (assoc coll key (conj (or (coll key) []) word)))))

(defn words->hmap [words]
  (reduce word-map-reducer {} words))

(defn update-all-words [words]
  (swap! all-words conj (words->hmap words)))

(defn process-new-words [words]
  (let [big-uns (filter #(> (count %) 10) words)
        small-fries (filter #(<= (count %) 10) words)]
    (do 
      (update-all-words big-uns)
      (p/update-all-prime-words small-fries))))

(defn reset-big-words []
  (reset! all-words {}))

(defn delete-all-words []
  (do
    (reset-big-words)
    (p/reset-small-words)))

(defn delete-big-word [word]
  (let [other-matches (match-words word)]
    (swap! all-words assoc (word->key word) other-matches)))

(defn delete-word [gross-word]
  (let [word (strip-ext gross-word)]
    (if (> (count word) 10)
      (delete-big-word word)
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
