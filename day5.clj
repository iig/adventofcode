;;day 5 really is there to show off threading and various seq filtering options
(->> (clojure.string/split-lines moarr)
     (filter #(re-find #"(\w)\1+" %))                 ;nasty regex lookback magic
     (remove #(re-find #"(xy)|(ab)|(cd)|(pq)" %))     
     (filter #(< 2 (count (re-seq #"[aeiou]" %))))    ;re-seq gives back all matches
     count)

;; you think regexes are evil? HA. not even close!
(->> (clojure.string/split-lines moarr)
     (filter #(re-find #"(\w)\w\1" %))                ;lookahead for a diet sandwich
     (filter #(re-find #"(\w\w).*\1" %))              ;lookahead for a double bread arbitrary large sandwich
     count)
