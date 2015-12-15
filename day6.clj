;; this will be more of a notebook format.
;; load the instruction file into instructions var

;; let's see how regexes do

(bench (->> (clojure.string/split-lines instructions)
                   (map (partial re-find #"([a-z ]+)\s(\d+),(\d+)[a-z ]+(\d+),(\d+)"))
                   (map (fn [[_ dir x1 y1 x2 y2]] {:dir dir :x1 x1 :y1 y1 :x2 x2 :y2 y2}))))
Evaluation count : 415440 in 60 samples of 6924 calls.
             Execution time mean : 145.435131 µs
    Execution time std-deviation : 889.790359 ns
   Execution time lower quantile : 144.556944 µs ( 2.5%)
   Execution time upper quantile : 147.895166 µs (97.5%)
                   Overhead used : 1.831614 ns

Found 5 outliers in 60 samples (8.3333 %)
	low-severe	 1 (1.6667 %)
	low-mild	 4 (6.6667 %)
 Variance from outliers : 1.6389 % Variance is slightly inflated by outliers
 
;; now that we know that, let's see if regexes can be beat (i'm guessing no)

(bench  (->> (clojure.string/split-lines instructions)
             (map #(subs % 5))
             (map #(clojure.string/split % #"\s|,"))
             (map (fn [[dir x1 y1 _ x2 y2]] {:dir dir :x1 x1 :y1 y1 :x2 x2 :y2 y2}))))
Evaluation count : 433680 in 60 samples of 7228 calls.
             Execution time mean : 138.947581 µs
    Execution time std-deviation : 1.501249 µs
   Execution time lower quantile : 137.809316 µs ( 2.5%)
   Execution time upper quantile : 143.671419 µs (97.5%)
                   Overhead used : 1.831614 ns

Found 7 outliers in 60 samples (11.6667 %)
	low-severe	 1 (1.6667 %)
	low-mild	 6 (10.0000 %)
 Variance from outliers : 1.6389 % Variance is slightly inflated by outliers

;; and this is why i should never guess
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; then we compose a parsing fuction:

(def parse-instructions (comp #(clojure.string/split % #"\s|,") #(subs % 5)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; and with that generating coordinates is just a matter of a for loop

(defn make-coordinates
  [x1 y1 x2 y2]
  (for [x (range (Integer/parseInt x1) (inc (Integer/parseInt x2))) 
        y (range (Integer/parseInt y1) (inc (Integer/parseInt y2)))] 
    [x y]))
    
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; so the naive solution is below
;; now i will argue, that for this problem, a thing that will have to be run only once
;; this is sufficient, it's simple, it's naive, and while it takes half a minute to run
;; it got the right answer

(def rules {"e"   #(if (= 1 %) 0 1)   ;if you dont' initalize your data structure with 0s you end up with nils and so (= 0 %) would do odd things
            "on"  (fn [_] 1)
            "off" (fn [_] 0)})

(time (reduce + (vals (reduce (fn [board [dir x1 y1 _ x2 y2]]
                                (reduce (fn [board xy] (update board xy (rules dir))) board (make-coordinates x1 y1 x2 y2)))
                              {}
                              (map parse-instructions (clojure.string/split-lines instructions))))))
"Elapsed time: 34739.182164 msecs"
=> 400410

;; and of course all you ahve to do to solve part 2 is to update you rules:
(def init #(if (nil? %) 0 %))
(def rules {"e" (comp inc inc init)
            "on" (comp inc init)
            "off" (comp #(if (zero? %) 0 (dec %)) init)})
            
"Elapsed time: 36868.053991 msecs"
=> 15343601

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ok. that was slow. can we make it faster?



