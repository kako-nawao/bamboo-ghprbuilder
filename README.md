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


## What Can It Do So Far?

You can configure the plans to build (plan and branch, that's it), and have them be
build *master* when you get a POST from GitHub. Useless, like I said.


## What Now?

In order of priority:

1. Build the proper branch (PR head)
2. Use proper authentication (we're using "admin" user as hack)
3. Update PR status on trigger
4. Update PR status on completion
