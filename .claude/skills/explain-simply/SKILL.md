---
name: explain-simply
description: Explain any concept, code, error, or topic in plain, jargon-free language with a diagram and a real-world analogy so the reader gets a clear mental picture fast, not just a wall of text. Use this by default whenever the user asks to "explain", "what is", "how does X work", "walk me through", "break this down", or "help me understand" — for code, error messages, architecture, algorithms, business concepts, or general topics. Trigger even when the user doesn't say the word "explain" but is clearly confused and wants to understand something (e.g. "wait why does this happen", "I don't get this part"). Do not use it for requests that are really asking for a code fix, a review, or an opinion rather than an explanation.
---

# Explain Simply

The goal is not to be _correct and complete_ — it's to be _understood on the first read_. A technically
accurate explanation that requires a second read has failed. Optimize for the reader forming a clear
picture in their head, covering every edge case.

## The three ingredients

Every explanation should have as many of these as are relevant — don't force all three if one doesn't fit:

1. **Plain language first.** Say it the way you'd explain it to a smart friend outside the field, over
   coffee, with no whiteboard. If a technical term is unavoidable (the reader will hit it elsewhere and
   needs to recognize it), define it in the same breath you introduce it — don't assume it's already known.
   Short sentences beat long ones. Concrete beats abstract.

2. **An analogy.** Map the unfamiliar thing onto something the reader already has intuition for
   (plumbing, mail, restaurants, traffic, a filing cabinet, a relay race — whatever fits). A good analogy
   does real work: it should make the _next_ paragraph easier to understand, not just decorate the first
   one. Pick an analogy where the mapping is tight — if you have to explain three ways the analogy breaks
   down, pick a different one.

3. **A diagram, when the thing has structure.** If the concept is a sequence, a flow, a hierarchy, a
   before/after, or a set of relationships between parts, draw it — don't describe a shape in prose when
   you can show it. If the concept is a single flat idea with no moving parts (e.g. "what is a prime
   number"), skip the diagram; forcing one adds noise instead of clarity.

Reach for whichever of these best fits the specific thing being explained. A recursive function benefits
from a call-stack diagram and an analogy (Russian nesting dolls, a queue at a photocopier). A business
concept like "runway" mostly needs plain language and an analogy (a bank account draining at a fixed
rate) — a diagram of a single number going down over time rarely adds much. Use judgment; don't pattern-
match mechanically.

## How to produce the diagram

- **Default to a Mermaid diagram rendered via the Artifact tool** (or inline in chat if the host renders
  Mermaid directly) — flowcharts for processes, sequence diagrams for interactions over time, and simple
  boxes-and-arrows for architecture or data flow. Load the `artifact-diagramming` skill first if one is
  available in this session — it has the mechanics for keeping diagrams legible in both light and dark
  themes.
- For something that's genuinely simpler as text-art (a small tree, a short pipeline of 3-4 steps), a
  clean ASCII/monospace diagram in a code block is fine and faster than firing up an artifact.
- Never produce a diagram that's just decoration — every box and arrow should map to something you
  actually explained in the prose. If you can't label an arrow with what it represents, cut it.

## Explaining code specifically

When the thing being explained is code:

- Walk through what happens when it _runs_, not line-by-line what each token means — trace the actual
  flow of data or control.
- Use a concrete example input if the code takes input, and show what comes out the other end.
- A sequence diagram or flowchart usually pays off here: show the order things happen in, especially
  across function/file boundaries, callbacks, or async steps — that's the part readers actually lose
  track of.
- Only show the parts of the code relevant to the confusion; don't re-paste the whole file.

## Length and shape

Keep it tight. A good explanation is usually: one or two plain-language sentences that state the core
idea, the analogy woven in naturally (not "Here's an analogy:" as its own section), a diagram if it
earns its place, and a short closing sentence that ties it back to whatever the user was actually asking
about. Avoid headers and bullet-point taxonomies for a straightforward explanation — that structure is
for reports, not for helping someone "get it." Reserve structure for genuinely multi-part concepts.

If the user's question is narrow (e.g. "what does this one error mean"), answer narrowly — don't expand
it into a lecture on the whole subsystem. Match the scope of the explanation to the scope of the
confusion.
