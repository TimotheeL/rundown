# Rundown

Rundown is a concise, flexible and human readable language to describe running workouts. Running is the only sport currently supported by rundown, although a lot of what can be written in rundown can probably apply to other sports, such as cycling or swimming.

## Motivation

The motivation behind rundown is to provide a universal format for presenting and sharing running workouts, that could be integrated with proprietary apps and platforms to easily create workouts ready to be synced to the watch. This would facilitate sharing between athletes / coaches / etc, who may not necessarily use the same platforms for their workouts.

By providing an easy to read, standard but also flexible format for running workouts, rundown can also provide some consistency over the titles athletes decide to give to their sessions on popular platforms such as Strava.

## Key Principles

- **Human Readable** : The main goal of rundown is the sharing of running workouts. Workouts should therefore be readable and intelligible, even with little to no knowledge of the rundown syntax;
- **Concise & Lightweight** : Any workout should be describable in as little characters as possible without compromising readability. There is no file extension for Rundown. Where workouts need to be shared as files, it is appropriate to use the `txt` extension;
- **Flexible** : Rundown should provide tools to allow for different ways for the same workouts to be defined, to cater for the needs of as many people as possible. While concision is an important principle, we also want to allow users to provide the level of detail they need for their training sessions.

## Specifications

The specs for rundown can be found [here](obsidian://open?vault=rundown&file=Specs).

## Version

This is a first draft. Any feedback is welcome!

## Examples

Here are a few valid rundown workout examples:

```
30mn steady
```

```
10 x 30", R=30"
```

```
warmup ; 5 x 1M
```

```
4 x 2km @(M-HM)P, R=2mn
```

```
10 x 800m track, S=3mn
```

```
3 x (9mn @MP; R=1:30; 5mn @10kP), R=5mn
```

```
15mn WU; 10 x 150m uphill @(160-170)bpm, R=(150m downhill @5:30mn/km); 15mn CD
```

