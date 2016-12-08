(ns adventofcode.x2016.day5
  (:require [clj-message-digest.core :as d]
            [clojure.core.async :as async :refer [<! <!! >! >!! go chan]]
            [clojure.edn :as edn]))

;; going for extra points with cinematic decryption :)
(def results (atom []))

(def in-chan (chan 25))

(go (while true
      (let [n (<! in-chan)]
            (dorun (map #(let [m (re-find #"^0{5}(.)"
                                          (d/md5-hex (str "uqwqemis" %)))]
                          (when m
                            (swap! results conj [% m])))
                        (range (* 10000 n) (* 10000 (+ n 1))))))))

(reduce (fn [_ x]
          (let [res @results]
            (if (> (count res) 7)
              (reduced res)
              (do (println [(apply str (map #(-> % last last) res)) x])
                  (>!! in-chan x))))) [] (range))

(->> (sort-by first @results) (map last) (map last) (take 8) (apply str))

;;;;;;;;;;;;;;;;;;; and again extra viz
(def results (atom []))

(def in-chan (chan 25))

(defn viz
  [data]
  (->> (map (fn [n]
              (sort-by first (filter #(= (second %) n) data)))
            (range 0 8))
       (remove empty?)
       (map first)
       (reduce (fn [spaces [_ pos char]]
                 (assoc spaces pos char)) ["-" "-" "-" "-" "-" "-" "-" "-"])
       (apply str)))

(go (while true
      (let [n (<! in-chan)]
        (dorun (map #(let [[hash pos char :as m] (re-find #"^0{5}(.)(.)"
                                                    (d/md5-hex (str "uqwqemis" %)))]
                      (when m
                        (swap! results conj [% (edn/read-string pos) char])))
                    (range (* 10000 n) (* 10000 (+ n 1))))))))

(reduce (fn [_ x] (do (println [(viz @results) x]) (>!! in-chan x))) [] (range))
