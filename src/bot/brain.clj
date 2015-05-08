(ns bot.brain
  (:require [clojure.data.priority-map :as priority-map]))

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
                   (or (= (- x tx) 0) (= (- y ty) 0))
                   (not= [x y] [tx ty])
                   (not= (nth (nth map ty) tx) \x)
                   (not (contains? closed [tx ty]))
                   )]
    [tx ty]))

(defn path [end parent closed]
  (reverse
    (loop [path [end parent]
           node (closed parent)]
      (if (nil? node)
        path
        (recur (conj path node) (closed node))))))

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

(defn next-move [{:keys [gameState playerState] :as state}]
  (let [tile-map (map #(mapv identity  %) (get-in gameState [:map :tiles]))
        exit (get-in gameState [:map :exit])
        {:keys [position]} playerState
        my-x (:x position)
        my-y (:y position)
        first-step (first (search tile-map [my-x my-y] [(:x exit) (:y exit)]))
        x (first first-step)
        y (second first-step)
        tx (- my-x x)
        ty (- my-y y)]
    (if (= tx 0)
      (if (< ty 0)
        "UP"
        "DOWN")
      (if (< tx 0)
        "LEFT"
        "RIGHT"))))