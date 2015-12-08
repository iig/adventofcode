;Solutions to day 1 of puzzle from adventofcode.com
;part 1:
(let [parens input
      counter (fn [re] (count (re-seq re parens)))]
  (- (counter #"\(")
     (counter #"\)")))

; or 

(let [counter (fn [re parens] (count (re-seq re parens)))
      up (partial counter #"\(")
      down (partial counter #"\)")]
  (- (up input) (down input)))

;and then came part 2 and i get punnished for using regexes... 

(reduce (fn [{:keys [pos sum]} sym] 
          (let [next (+ sum (if (= \( sym) 1 -1))]
            (if (= -1 next)
              (reduced pos)
              {:pos (inc pos) :sum next}))) 
        {:pos 1 :sum 0} 
        input)
