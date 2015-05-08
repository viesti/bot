(ns bot.brain
  (:require [clojure.data.priority-map :as priority-map]
            [clojure.pprint :refer [pprint]])
  (:import [CDIO.pathFinder AreaMap AStar Path]
           [CDIO.pathFinder.heuristics AStarHeuristic DiagonalHeuristic]))

(defn manhattan-distance [[x1 y1] [x2 y2]]
  (+ (Math/abs ^Integer (- x2 x1)) (Math/abs ^Integer (- y2 y1))))

(defn cost [curr start end]
  (let [g (manhattan-distance start curr)
        h (manhattan-distance curr end)
        f (+ g h)]
    [f g h]))

(defn edges [map width height closed [x y]]
  (for [tx (range (- x 1) (+ x 2))
        ty (range (- y 1) (+ y 2))
        :when (and (>= tx 0)
                   (>= ty 0)
                   (<= tx width)
                   (<= ty height)
                   #_(or (zero? (- x tx)) (zero? (- y ty)))
                   (not= [x y] [tx ty])
                   (not= (nth (nth map ty) tx) \x)
                   (not (contains? closed [tx ty])))]
    [tx ty]))

(defn path [end parent closed]
  (reverse
    (loop [path [end parent]
           node (closed parent)]
      (if (nil? node)
        path
        (recur (conj path node) (closed node))))))

(def foo (atom 0))

(defn search
  ([map start end]
   (let [[sx sy] start
         [ex ey] end
         open (priority-map/priority-map-by
                (fn [x y]
                  (if (= x y)
                    0
                    (let [[f1 _ h1] x
                          [f2 _ h2] y]
                      (if (= f1 f2)
                        (if (< h1 h2) -1 1)
                        (if (< f1 f2) -1 1)))))
                start (cost start start end))
         closed {}
         width (-> map first count dec)
         height (-> map count dec)]
     (when (and (not= (nth (nth map sy) sx) 1)
                (not= (nth (nth map ey) ex) 1))
       (search map width height open closed start end))))

  ([map width height open closed start end]
   (if-let [[coord [_ _ _ parent]] (peek open)]
     (if-not (= coord end)
       (let [closed (assoc closed coord parent)
             edges (edges map width height closed coord)
             open (reduce
                    (fn [open edge]
                      (if (not (contains? open edge))
                        (assoc open edge (conj (cost edge start end) coord))
                        (let [[_ pg] (open edge)
                              [nf ng nh] (cost edge start end)]
                          (if (< ng pg)
                            (assoc open edge (conj [nf ng nh] coord))
                            open))))
                    (pop open) edges)]
         (recur map width height open closed start end))
       (path end parent closed)))))

(def maze1 [[0 0 0 0 0 0 0]
            [0 0 0 \x 0 0 0]
            [0 0 0 \x 0 0 0]
            [0 0 0 \x 0 0 0]
            [0 0 0 0 0 0 0]])

(defn available? [board [x y]]
  (not= \x (nth (nth board y) x)))

(defn calculate-move [board [x y] [tx ty]]
  (let [x-dir (Integer/signum (- tx x))
        y-dir (Integer/signum (- ty y))]
    (let [next-x [(+ x x-dir) y]
          next-y (if (not (zero? x-dir))
                   (if (< y (/ (count board) 2))
                     [x (dec y)]
                     [x (inc y)])
                   [x (+ y y-dir)])
          next (if (or (zero? x-dir) (not (available? board next-x))) next-y next-x)]
      next)))

(defn get-coordinates [item]
  (when item
    (let [x (get-in item [:position :x])
          y (get-in item [:position :y])]
      [x y])))

(defn select-target [items money my-pos]
  (pprint money)
  (let [items-with-dist (map (fn [item]
                               (let [item-pos (:position item)]
                                 (assoc item :distance (manhattan-distance my-pos [(:x item-pos) (:y item-pos)])))) items)]
    (->> items-with-dist
         (sort-by :distance)
         (remove #(< money (* (:price %) (- 100 (:discountPercent %)) 0.01)))
         first
         get-coordinates)))

(defn obstacles [tiles]
  (into-array (map (fn [row]
                     (into-array Integer/TYPE (map #(if (= % \x) 1 0) row)))
                   tiles)))

(defn java-a* [tiles sx sy tx ty]
  (let [area (AreaMap. (count (first tiles)) (count tiles) (obstacles tiles) #_(make-array Integer/TYPE w h))
        heuristic (DiagonalHeuristic.)
        astar (AStar. area heuristic)
        path (.calcShortestPath astar sx sy tx ty)]
    (map (fn [p] [(.x p) (.y p)]) path)))

(defn next-move [{:keys [gameState playerState] :as state}]
  (let [tile-map (mapv vec (get-in gameState [:map :tiles]))
        exit (get-in gameState [:map :exit])
        items (:items gameState)
        {:keys [position money]} playerState
        my-x (:x position)
        my-y (:y position)
        my-coords [my-x my-y]
        target (or (select-target items money my-coords) [(:x exit) (:y exit)])]
    (pprint target)
    (if (= my-coords target)
      "PICK"
      (do
        (pprint state)
        (pprint my-coords)
        (let [first-step (first (java-a* (get-in gameState [:map :tiles]) my-x my-y (first target) (second target)))
              x (first first-step)
              y (second first-step)
              tx (- x my-x)
              ty (- y my-y)]
          (if (= tx 0)
            (if (< ty 0)
              "UP"
              "DOWN")
            (if (< tx 0)
              "LEFT"
              "RIGHT")))))))
