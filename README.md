
It's basically an experiment that I started playing with in different forms long before reactive was a thing and that I now know would be described as a pull-based reactive framework.

I lack experience with reactive frameworks, but I think the style of this one is considerably different. Value/expression dependencies are automatic (you need not worry about managing them) and dynamic (meaning they can change all the time). Cyclic dependencies can't work, and the framework does nothing to help you with that.

It combines this with a property pattern. A "model" object, a hierarchical reactive key-value store.

It's a concept thing, a work in progress.

As out-of-fashion non-web ui frameworks are these days, I still toy with the idea of building a ui framework on top of this.
