(ns adventofcode.day7
  (:require [clojure.zip :as zip]))


;; some helpers
(defn znot
  [x]
  (+ 65536 (bit-not x)))

(defn zrshift
  [n x]
  (bit-shift-right x n))

(defn zlshift
  [n x]
  (bit-shift-left x n))

(def zand bit-and)
(def zor bit-or)

;; so here's a little parser

(defn parser
  [instructions]
  (let [re-find-groups #(rest (re-find %1 %2))
        one-arg-op (partial re-find-groups #"(\w+) ->")
        shift-op (partial re-find-groups #"(\w+)[A-Z ]+(\d+)")
        two-arg-op (partial re-find-groups #"(\w+)\s\w+\s(\w+)")
        parse-shift (fn [f s]
                      (let [[var num] (shift-op s)]
                        {:op (partial f (Integer/parseInt num)) :args [var]}))]
    (reduce (fn [x s] (assoc x (last (re-find #"-> (\w+)" s))
                               (assoc (cond
                                        (re-find #"1\sAND" s) {:op (partial zand 1) :args (one-arg-op s)}
                                        (re-find #"AND" s) {:op zand :args (two-arg-op s)}
                                        (re-find #"OR" s) {:op zor :args (two-arg-op s)}
                                        (re-find #"LSHIFT" s) (parse-shift zlshift s)
                                        (re-find #"RSHIFT" s) (parse-shift zrshift s)
                                        (re-find #"NOT" s) {:op znot :args (one-arg-op s)}
                                        (re-find #"\d" s) {:op (partial identity (-> (one-arg-op s) first Integer/parseInt))}
                                        :else {:op identity :args (one-arg-op s)})
                                 :xstr s)))
            {} (clojure.string/split-lines instructions))))

;; and a little recursive solve
;; i tried doing this without the cache but it wasn't exactly performant so

(defn xsolve
  [instr key cache]
  (let [{:keys [op xstr args]} (get instr key)
        res (if args
              (apply op (map #(if-let [cached (get @cache %)]
                                cached
                                (xsolve instr % cache)) args))
              (op))
        _ (swap! cache assoc key res)]
    res))

(defn solve
  [instr key]
  (xsolve instr key (atom {})))

;; if you want to see what is actually is going on here's a chatty version

(def instructions "paste them here")

(defn debug-solve
  [instr key cache]
  (let [{:keys [op xstr args]} (get instr key)
        _ (println xstr)
        res (if args
              (apply op (map #(if-let [cached (get @cache %)]
                               (let [_ (println (str "Cache retreived for " %))] cached)
                               (let [_ (println (str "Failed to `decache` " %))] (xsolve instr % cache))) args))
              (op))
        _ (println (str "Writing to cache " key " = " res))
        _ (swap! cache assoc key res)]
    res))


;;;;;;;;; and then to run it just do
(solve (parser instructions) "a")

;;;;;;;;; for part two it's a little ridiculously easy
(solve (parser (str instructions "\n" (solve (parser instructions) "a") " -> b")) "a")
