(ns adventofcode.x2016.day7
  (:require [clojure.string :as str]
            [net.cgrand.xforms :as x]))

(def data "abba[mnop]qrst\nabcd[bddb]xyyx\naaaa[qwer]tyui\nioxxoj[asdfgh]zxcvbn")
;; part 1. regex black magic
(into [] (comp
           (filter (partial re-find #"(\w)(?!\1)(\w)(?=\2).(?=\1)"))
           (remove (partial re-find #"\[\w*(\w)(?!\1)(\w)(?=\2).(?=\1).\w*\]"))
           x/count)
      (str/split-lines data))

;; part 2. regex for this might be a bit too crazy so let's fix the strings first
(defn distill
  "Split the string into outside-of-bracket and inside-of-brackets part separated by ["
  [s]
  (let [dash-join-last #(str/join "-" (map last %))]
    (str (dash-join-last (concat (re-seq #"\]?(\w+)\[" s)
                                 (re-seq #"\](\w+)$" s)))
         "["
         (dash-join-last (re-seq #"\[(\w+)\]" s)))))

(into [] (comp
           (map distill)
           (filter (partial re-find #"(\w)(?!\1)(\w)(?=\1).+\[.*(?=\2).(?=\1).(?=\2)"))
           x/count
           )
      (str/split-lines data))