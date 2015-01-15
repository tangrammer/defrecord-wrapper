![image](https://dl.dropboxusercontent.com/u/8688858/epicarp.gif)

# defrecord-wrapper

This library lets you apply middleware to protocol implementations of [clojure.core/defrecord](https://clojuredocs.org/clojure.core/defrecord) in the same way as AOP does.

As wikipedia defines [AOP](http://en.wikipedia.org/wiki/Aspect-oriented_programming): 
> In computing, aspect-oriented programming (AOP) is a programming paradigm that aims to increase modularity by allowing the separation of cross-cutting concerns. AOP forms a basis for aspect-oriented software development.   
> ...   
> Logging exemplifies a crosscutting concern because a logging strategy necessarily affects every logged part of the system. Logging thereby crosscuts all logged classes and methods....

**Common and practical scenario**   
Working with [juxt/modular](https://github.com/juxt/modular) or directly  [stuartsierra/component](https://github.com/stuartsierra/component) enforces you to work with defrecords (or simply maps). Using this defrecord-wrapper dependency you can totally or partially intercept your protocols functions 


#### Releases and Dependency Information


```clojure

[tangrammer/defrecord-wrapper "0.1.6"]

```

```clojure
:dependencies [[org.clojure/clojure "1.6.0"]]
```

## Usage

```clojure
(ns your-ns
  (:require [defrecord-wrapper.aop :as aop]
            [defrecord-wrapper.reflect :as r])
  (:import [defrecord_wrapper.aop SimpleWrapper]))

;; here your protocol, defrecord definitions and defrecord instance

(defprotocol Welcome
  (greetings [e] )
  (say-bye [e a b])
  )

(defrecord Example []
  Welcome
  (greetings [this] "my example greeting!")
  (say-bye [this a b] (str "saying good bye from " a " to " b)))

(def my-example-instance (Example.))

;; here your fn middleware to apply

(defn logging-access-protocol
  [*fn* this & args]
  (println ">> LOGGING-ACCESS " [this args])
  (println ">>"(meta *fn*))
  (apply *fn* (conj args this)))


;; here extending SimpleWrapper with your defrecord instance functions protocols

(aop/add-extends SimpleWrapper (r/get-specific-supers my-example-instance) logging-access-protocol)

;; here wrapping your defrecord instance with SimpleWrapper 

(def my-wrapped-example (SimpleWraper. my-example-instance))


;; and ... invoking wrapper

(println (greetings my-wrapped-example))

;;=> >> LOGGING-ACCESS  [#yourapp.your_ns.Example{} nil]
;;=> >> {:function-args [e], :wrapper #defrecord_wrapper.aop.SimpleWrapper{:wrapped-record #yourapp.your_ns.Example{}}, :function-name greetings}
;;=> my example greeting!


(println (say-bye my-wrapped-example "clojure" "java"))
;;=> >> LOGGING-ACCESS  [#yourapp.your_ns.Example{} (clojure java)]
;;=> >> {:function-args [e a b], :wrapper #defrecord_wrapper.aop.SimpleWrapper{:wrapped-record #yourapp.your_ns.Example{}}, :function-name say-bye}


```
### Matchers available in tangrammer/defrecord-wrapper
Due that milesian/aop actually uses [tangrammer/defrecord-wrapper](https://github.com/tangrammer/defrecord-wrapper/), there are a few special matchers  for free that you can be intereseted on using:
+ `nil` value that returns nil
+ `fn` value  that returns itself, (it's a shortcut to apply your-fn-middleware for all fns protocol)
+ `defrecord-wrapper.aop/SimpleProtocolMatcher` that returns your-fn-middleware when the protocol of the fn invoked matchs with any of the the protocols provided

### SimpleProtocolMatcher implementation
Instead of plain functions or your own implementations of [Matcher](https://github.com/tangrammer/defrecord-wrapper/blob/master/src/defrecord_wrapper/aop.clj#L4) protocol, you can also use the SimpleProtocolMatcher as follows

```clojure

(aop/add-extends SimpleWrapper 
    (r/get-specific-supers my-example-instance) 
    (aop/new-simple-protocol-matcher 
        :protocols [Welcome] 
        :fn logging-access-invocation))
```


For more detailed matching there is also a [bidi-wrapper-matcher](https://github.com/tangrammer/bidi-wrapper-matcher), an implementation using the  [juxt/bidi](https://github.com/juxt/bidi) way

### Related projects
* [milesian/aop](https://github.com/milesian/aop): facility to apply defrecord-wrapper in stuartsierra/component library


## License

Copyright © 2014 Juan Antonio Ruz (juxt.pro)

Distributed under the [MIT License](http://opensource.org/licenses/MIT). This means that pieces of this library may be copied into other libraries if they don't wish to have this as an explicit dependency, as long as it is credited within the code.   

Copyright "Hesperidium" image @ [clipart](http://etc.usf.edu/clipart/)
