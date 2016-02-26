# bamboo-ghprbuilder

A GitHub Pull Request Builder servlet for Bamboo.

I'm working on it, it's not even close to be ready yet. Like 10% done. And my
Java is **very** rusty, so progress is rather slow.

Also, Atlassian SDK documentation is obscenely outdated, pretty much useless, so
it's mostly trial and error unfortunately.


## Why?

Because I really like Jenkins' GitHub Pull Request Builder plugin, and I feel
that something like that is necessary for any decent gitflow-driven development
process. Then why not just use Jenkins? We're using it at work, not my choice,
but I actually like it, it's *cleaner*. Also, I felt it was an interesting
personal project.


## How?

AFAIK Bamboo does not let you customize builders, so the approach has to be rather
different. In theory, it would (will?) consist of three components:

* Config servlet: to store configuration such as plans to build on PRs
* PR Servlet: to process GitHub webhooks and trigger builds
* Post-Build action: to post results back to GitHub

Ideally, I'd like to include the last step when the thing is triggered, without
requiring an extra job/task in the plan, to simplify configuration.
Because if there's one thing I dislike about Jenkins' plugin is the fact that
you need to configure many things twice.


## What Is Done?

1. Add plans to auto-build
2. Build correct revision on POST from github
3. Use proper authentication

The *baseline branch* is taken from the plan's repository configuration, so if
your repo has branch *develop*, builds will only be triggered for pull request
to merge to *develop*, ignoring any other requests.

Builds are executed by the user that created every pull request builder. So if
*Peter* adds a builder for *Plan X*, then pull requests' for *Plan X* are
built by *Peter*.


## What Remains to Do?

In order of priority:

1. Update PR status on trigger
2. Update PR status on completion
3. Trigger all repos (branches) in the plan
