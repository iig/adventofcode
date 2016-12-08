(ns adventofcode.x2016.day3
  (:require [clojure.string :as str]
            [net.cgrand.xforms :as x]
            [clojure.core.matrix]))

(def data "")

(into [] (comp (map (partial re-seq #"\d+"))
               (map #(map (fn [x] (Integer/parseInt x)) %))
               (map sort)
               (map (fn [[a b c :as input]] (when (> (+ a b) c) input)))
               (remove nil?)
               x/count) (str/split-lines data))

(-> (into [] (comp (map (partial re-seq #"\d+"))
                   (map #(map (fn [x] (Integer/parseInt x)) %)))
          (str/split-lines data))
    clojure.core.matrix/transpose
    flatten
    (->> (partition 3)
         (into [] (comp
                    (map sort)
                    (map (fn [[a b c :as input]] (when (> (+ a b) c) input)))
                    (remove nil?)
                    x/count))))