(ns managrams.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [clojure.string :as str]
            [managrams.processor :as pro]))

(defn strip-ext [word]
  (str/lower-case (first (str/split word #"\."))))

(defapi app
  (POST "/words.json" []
    :body-params [words :- [String]]
    {:status 201 :body {:words (pro/process-new-words words)}})

  (DELETE "/words/:word" [word]
    {:status 204 :body {:deleted (pro/delete-word (strip-ext word))}})

  (DELETE "/words.json" []
    {:status 204 :body {:deleted (pro/delete-all-words)}})

  (GET "/maximum.json" []
    {:status 200 :body {:words (pro/maximum-anagrams)}})

  (context "/anagrams" []
    (GET "/:word" [word]
      :query-params [{limit :- Long -1}]
      :return {:anagrams [String]}
      (ok {:anagrams (pro/anagrams-for (strip-ext word) limit)}))
    
    (DELETE "/:word" [word]
      {:status 204 
       :body {:deleted (pro/delete-word-and-anagrams (strip-ext word))}})))
