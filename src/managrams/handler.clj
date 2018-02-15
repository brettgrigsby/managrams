(ns managrams.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [clojure.string :as str]
            [clojure.core.reducers :as r]))

(def all-words (atom {}))

(def word-map {"ader" ["dare" "dear"]})

(defn word->key [word]
  (str/join (sort word)))

(defn match-words [word]
  (remove #(= word %) (@all-words (word->key word))))

(defn strip-ext [word]
  (first (str/split word #"\.")))

(defn anagrams-for [gross-word limit]
  (let [word (strip-ext gross-word)
        matching-words (match-words word)]
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

(defn delete-all-words []
  (reset! all-words {}))

(defn delete-word [gross-word]
  (let [word (strip-ext gross-word)
        other-matches (match-words word)]
    (swap! all-words assoc (word->key word) other-matches)))

(defapi app
  (GET "/current-words.json" []
    :return {:words String}
    (ok (:words @all-words)))

  (POST "/words.json" []
    :body-params [words :- [String]]
    {:status 201 :body {:words (update-all-words words)}})

  (DELETE "/words/:word" [word]
    {:status 204 :body {:deleted (delete-word word)}})

  (DELETE "/words.json" []
    {:status 204 :body {:deleted (delete-all-words)}})

  (context "/anagrams" []
    (GET "/:word" [word]
      :query-params [{limit :- Long -1}]
      :return {:anagrams [String]}
      (ok {:anagrams (anagrams-for word limit)}))))
