
(development continued on gitlab. the state of this code is more concept than anything else.)

It's an experiment that I started playing with in different forms long before reactive was a thing and that now could be described as a pull-based reactive framework.

The style of this one is different. Value and expression dependencies are automatic (you do not need to worry about managing them) and dynamic (meaning the dependency graphs change continuously at runtime). Cyclic dependencies will fail, and the framework does nothing to help you with that.

There's also the concurrency model to think of. How does one get a consistent view? There are different ways. Software transactional memory is one, processing the graph in an event loop (actor based) is another. The framework has an implementation for either. (stm depends on akka stm)

There's an implementation of a property pattern. A "model" object, a hierarchical reactive key-value store.

It's a proof of concept, a work in progress. It's not finished. There are some concerns and ideas that need work. How transactional snapshots of values cross domain boundaries. Inter-domain flows.

As out-of-fashion non-web ui frameworks are these days, I still play with the idea of building a rich ui framework on top of this. Because it could take the pain out of ui development. (in fact classic user interface frameworks are a big example of why this is

I have also thought of writing an annotation processor and annotations to generate code that would reduce plumbing. Moving into DSL-country. Ultimately, you'd really want the concept to be baked into a mainstream language though.
