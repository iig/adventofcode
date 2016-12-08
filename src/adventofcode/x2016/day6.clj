(ns adventofcode.x2016.day6
  (:require [clojure.string :as str]
            [clojure.core.matrix]))
;;;part 1
(->> (str/split-lines xdatum)
     (map (partial into []))
     (clojure.core.matrix/transpose)
     (map frequencies)
     (map (partial sort-by last))
     (map last)
     (map first)
     (apply str))
;;; part 2
(->> (str/split-lines xdatum)
     (map (partial into []))
     (clojure.core.matrix/transpose)
     (map frequencies)
     (map (partial sort-by last))
     (map first)
     (map first)
     (apply str))