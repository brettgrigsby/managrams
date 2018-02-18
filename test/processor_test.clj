(ns processor-test
  (:use clojure.test)
  (:require [managrams.processor :as pro]))

(defn testing-fixture [f]
  nil
  (f)
  (pro/delete-all-words))

(use-fixtures :each testing-fixture)

(def mix-length-words ["pasta" "basketballs" "bread" "anagramming" "salad" "tenletters"])

(def small-anagrams ["read" "dear" "dare"])

(def big-anagrams ["excitations" "intoxicates"])

(deftest filter-words-by-lenth-ten
  (is (= ["pasta" "bread" "salad" "tenletters"] (pro/filter-words <= mix-length-words)))
  (is (= ["basketballs" "anagramming"] (pro/filter-words > mix-length-words))))

(deftest adds-words
  (pro/process-new-words small-anagrams)
  (is (= ["dare" "read"] (pro/anagrams-for "dear" -1)))
  (is (= ["dare" "dear"] (pro/anagrams-for "read" -1)))
  (is (= ["dare"] (pro/anagrams-for "dear" 1))))

(deftest handles-capitals
  (pro/process-new-words ["Married" "AdmiREr"])
  (is (= ["admirer"] (pro/anagrams-for "married" -1))))

(deftest deletes-single-words
  (pro/process-new-words small-anagrams)
  (is (= ["dare" "read"] (pro/anagrams-for "dear" -1)))

  (pro/delete-word "read")
  (is (= ["dare"] (pro/anagrams-for "dear" -1))))

(deftest deletes-all-words
  (pro/process-new-words small-anagrams)
  (pro/process-new-words big-anagrams)
  (is (= ["dare" "read"] (pro/anagrams-for "dear" -1)))
  (is (= ["intoxicates"] (pro/anagrams-for "excitations" -1)))
  
  (pro/delete-all-words)
  (is (= [] (pro/anagrams-for "dear" -1)))
  (is (= [] (pro/anagrams-for "excitations" -1))))

(deftest deletes-word-and-anagrams
  (pro/process-new-words small-anagrams)
  (pro/process-new-words big-anagrams)
  (is (= ["dare" "read"] (pro/anagrams-for "dear" -1)))
  (is (= ["intoxicates"] (pro/anagrams-for "excitations" -1)))
  
  (pro/delete-word-and-anagrams "dear")
  (is (= [] (pro/anagrams-for "dear" -1)))
  (is (= [] (pro/anagrams-for "read" -1)))
  (is (= ["intoxicates"] (pro/anagrams-for "excitations" -1))))

(deftest finds-maximum-anagrams
  (pro/process-new-words small-anagrams)
  (pro/process-new-words big-anagrams)
  (pro/process-new-words mix-length-words)
  
  (is (= #{"dare" "dear" "read"} (pro/maximum-anagrams))))
