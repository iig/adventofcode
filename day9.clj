(ns adventofcode.day9
  (:require [clojure.math.combinatorics :as combo]))

;; i take your np-complete problem and throw Moore's law at it and hope that problem space is small enough. 

;; let's parse the input
(def distances
  (->> (clojure.string/split-lines instr)
       (map #(clojure.string/split % #" "))
       (reduce (fn [all [from _ to _ d]]
                 (assoc-in (assoc-in all [from to] (Integer/parseInt d))
                           [to from] (Integer/parseInt d))) {})))

;; and now the combinatorics library to the rescue

(->> (combo/permutations (keys distances))                  ;find all permutations
     (map (fn [z] (apply + (map #(get-in distances %) (partition 2 1 z))))) ;split each permutation into pairs and add distances
     sort
     first)

;;the second part was rather easy

(->> (combo/permutations (keys distances))
     (map (fn [z] (apply + (map #(get-in distances %) (partition 2 1 z)))))
     sort
     last)
