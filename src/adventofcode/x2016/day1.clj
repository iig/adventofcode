(ns adventofcode.x2016.day1
  (:require [clojure.string :as str]
            [net.cgrand.xforms :as x]))
(def directions "L2, L3, L3, L4, R1, R2, L3, R3, R3, L1, L3, R2, R3, L3, R4, R3, R3, L1, L4, R4, L2, R5, R1, L5, R1, R3, L5, R2, L2, R2, R1, L1, L3, L3, R4, R5, R4, L1, L189, L2, R2, L5, R5, R45, L3, R4, R77, L1, R1, R194, R2, L5, L3, L2, L1, R5, L3, L3, L5, L5, L5, R2, L1, L2, L3, R2, R5, R4, L2, R3, R5, L2, L2, R3, L3, L2, L1, L3, R5, R4, R3, R2, L1, R2, L5, R4, L5, L4, R4, L2, R5, L3, L2, R4, L1, L2, R2, R3, L2, L5, R1, R1, R3, R4, R1, R2, R4, R5, L3, L5, L3, L3, R5, R4, R1, L3, R1, L3, R3, R3, R3, L1, R3, R4, L5, L3, L1, L5, L4, R4, R1, L4, R3, R3, R5, R4, R3, R3, L1, L2, R1, L4, L4, L3, L4, L3, L5, R2, R4, L2")

;  N
;W   E
;  S

; let's say we keep N & E as coordinates
; figuring out the actual direction is a reduction over a sequence. Then it can be partitioned and summed

(defn distance
  [{:keys [N E S W] :or {N 0 E 0 S 0 W 0}}]
  (+ (Math/abs (- N S))
     (Math/abs (- E W))))

(defn xr [[d] [x & n]]
  [(get-in {\L {:N :W, :W :S, :S :E, :E :N}
            \R {:N :E, :W :N, :S :W, :E :S}} [x d])
   (Integer/parseInt (apply str n))])

(distance (into {} (comp (reductions xr [:N 0])
                         (x/by-key (x/reduce +)))
                (str/split "R5, L5, R5, R3" #",\s")))


;; son of bumblebee... they siwtch it on you....

(defn coordinate-maker [a b]
  (if (= a b) (repeat a)
              (if (< a b)
                (range a (inc b))
                (range b (inc a)))))

(defn xr [[last-direction [last-x last-y] coordinates] [lr & n]]
  (let [next-dir (get-in {\L {:N :W, :W :S, :S :E, :E :N}
                          \R {:N :E, :W :N, :S :W, :E :S}} [lr last-direction])
        distance (Integer/parseInt (apply str n))
        [next-x next-y :as next-c]
        (case next-dir
          :N [last-x (+ last-y distance)]
          :S [last-x (- last-y distance)]
          :E [(+ last-x distance) last-y]
          :W [(- last-x distance) last-y])
        res (reduce (fn [a [x y :as xy]]
                      (if (contains? a xy)
                        (reduced (+ (Math/abs x)
                                    (Math/abs y)))
                        (conj a xy)))
                    coordinates
                    (map vector
                         (coordinate-maker last-x next-x)
                         (coordinate-maker last-y next-y)))]
    (if (nil? res)
      (reduced res)
      [next-dir next-c res])))


(into [] (comp (x/reductions xr [:N [0 0] #{[0 0]}])
               ;   (x/by-key (x/reduce +))
               )
      (str/split "R5, L5, R5, R3" #",\s"))
