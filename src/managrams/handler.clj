(ns managrams.handler
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [managrams.processor :as pro]))

(defapi app
  (POST "/words.json" []
    :body-params [words :- [String]]
    {:status 201 :body {:words (pro/process-new-words words)}})

  (DELETE "/words/:word" [word]
    {:status 204 :body {:deleted (pro/delete-word word)}})

  (DELETE "/words.json" []
    {:status 204 :body {:deleted (pro/delete-all-words)}})

  (context "/anagrams" []
    (GET "/:word" [word]
      :query-params [{limit :- Long -1}]
      :return {:anagrams [String]}
      (ok {:anagrams (pro/anagrams-for word limit)}))))
