Rundown is a lightweight, flexible and human readable running workout description language.

## Workout

A rundown workout is a single `UTF-8` string of text including all information relevant to a training session. Unless stated otherwise, everything in a workout is **case sensitive**.

A workout is made-up of **sections**. Each section in a workout is separated by a semicolon `;`. A workout can consist of a single section, in which case no semicolon is required.

*Example*

```
WU ; 7 x 2mn, R=1mn ; CD @5:30/km
```

The above workout is made of 3 sections:

- Section 1: `WU`
- Section 2: `7 x 2mn, R=1mn`
- Section 3: `CD @5:30/km`

## Section

A section is made up of **components**. There are 4 types of components: **Action**, **metadata**, **target** and **recovery**. These components must always appear in this order, but none of them are mandatory. The **main component** is the one that appears first. A section can have as little as one component. **Target** is the only type of component that can **not** be used as the main component of a section.

Components can be separated by whitespaces or commas. By convention, the **recovery** component is usually the only one separated from the rest of the workout by a comma.

*Example*

```
10 x 400m track @(75-80)s, R=2mn
```

- Action: `10 x 400m`
- Metadata: `track`
- Target: `@(75-80)s`
- Recovery: `R=2mn`

## Action Component

TODO

## Metadata Component

The **metadata** component is typically a keyword that provides relevant information about a section (e.g whether it is to be run on track or on hills). It is rarely used as the main component of a section. When (and only when) used as the main component, a multiplier can be used:

```
n x keyword
```

*Examples*

```
6 x strides
3 x uphill, R=downhill
```

More than one keyword may be used as part of a metadata component.

*Examples*

```
hilly warmup
downhill strides
```

Since metadata is only here for the benefit of the runner, rundown is not concerned with whether keywords are combined in a way that is valid or makes sense.

### Available Metadata Keywords

| Keyword          |
| ---------------- |
| `cooldown`, `CD` |
| `downhill`       |
| `easy`           |
| `hard`           |
| `hilly`          |
| `steady`         |
| `strides`        |
| `tempo`          |
| `track`          |
| `uphill`         |
| `warmup`, `WU`   |

## Target Component

A target component can be used to indicate a target pace, heart rate, power, expected time or other metric (see **Available Targets**).

Targets are identified with the `@` character and are optional. In practice, they look like:

```
@{target}
```

*Example*

```
@7:10/M
```

Reads as "at 7:10 minutes per mile pace"

> **_NOTE:_**  For available time and distance units and formatting, see **Time and Distance**.

### Application

The target component applies to the main component of the section it is in. If the main component of the section is an **action component**, the target applies to all sections in the component. However, if a target is also specified for an inner section, then the inner target takes precedence.

*Example*

```
2 x (10 x 30" @VO2max, R=30") @MP, R=5mn 
```

The target component for the above section is `@MP`, however a target (`@VO2max`) is also set for the inner section in the main component of the section. The real target of the section is therefore `@VO2max`, and `@MP` is ignored.

### Available Targets

| Target | Description |
| ---- | ---- |
| `{heartrate}bpm` | Heartrate, in beats per minute, e.g `150bpm` |
| `LT1` | Lactate threshold 1 |
| `LT2` | Lactate threshold 2 |
| `Z{zone}` | Heart rate zone, where `{zone}` is a digit, e.g `Z4`* |
| `VO2max` | Velocity at maximal oxygen uptake |
| `MP` | Marathon race pace |
| `HMP` | Half-Marathon race pace |
| `{distance}P` | Given distance race pace. For example: `10kmP`, `5kP`, `5MP`, `400mP` |
| `{time}` | Time for each rep, e.g `45s`, `1mn` |
| `tempo` | Tempo pace (somewhere between `10kP` and `HMP`) |
| `{time}/{distance}` | Running pace, e.g `4mn/km`, `5:00/k`, `7mn10/M` |
| `{distance}/{time}` | Running speed, e.g `16km/h` |
| `{power}W` | Running power, in Watt,. e.g `400W` |
| `{steps}spm` | Running cadence, in steps per minute, e.g `180spm` |
\* There are different heart rate zone models. Rundown is not concerned with which one the runner uses. This interpretation is left to the runner, or to the developer if they have access to this information.

### Ranges and Progressions

In addition to being able to define single value targets, it is also possible to define ranges and progressions to serve as targets.

#### Ranges

A range can be used to define a target range within which to run each rep the target applies to. They require defining a lower and an upper bound for the range, separated by the `-` character. Ranges read as "Any effort between the lower bound and the upper bound", where the effort can be anything defined in the available targets above (a pace, a time, a heartrate...).

Ranges look like this:

```
@{lower-bound}-{upper-bound}
```

*Example*

```
@4:40/km-4:20/km
```

- Reads as "any pace between 4:40 and 4:20mn per km".

When the unit is shared between the upper and lower bound of the range, as is often the case, it is preferable to factor out the unit using parentheses:

```
@({lower-bound}-{upper-bound}){bound-unit}
```

The above example can therefore be re-written as follows, which is more concise and readable:

```
@(4:40-4:20)/km
```

Other examples:

```
@(60-70)s           # between 60 and 70 seconds per rep
@(130-140)bpm       # between 130 and 140 bpm
@(HM-M)P            # between half-marathon and marathon pace
@LT1-LT2            # between lactate thresholds 1 and 2
@10k/h-7M/h         # between 10km per hour and 7 Miles per hour*
@Z3-Z4              # between heartrate zones 3 and 4
@Z4-VO2max          # between heartrate zone 4 and VO2 Max speed

*We hope no-one reading this document will be tempted to mix units in this awful manner, but then again, this is legal in rundown, so do as you wish.
```

Note that the lower target doesn't necessarily have to be declared first. `@(130-140)bpm` is equivalent to `@(140-130)bpm`.

#### Progressions

Progressions can be used to indicate that reps are to be run progressively, from a starting target to a finishing target. Progressions are defined in the same way ranges are defined, with the `>`  character used to separate the starting target from the finishing target:

```
@{starting-target}>{finishing-target}
```

*Example*
```
@4:40/km>4:20/km
```

- Reads as "run progressively from 4:40mn/km to 4:20mn/km"

As for ranges, units shared between the starting and ending target can be factored out. The above example can be rewritten as:

```
@(4:40>4:20)/km
```

While the starting target is always specified first, nothing prevents defining decreasing progressions (i.e starting from a faster pace and ending on a lower pace)

## Recovery Component

Recovery indicates how to recover and for how long. The syntax for recovery is as follows:

```
R={recovery}
```

Where `{recovery}` is a **section** without the **recovery component**, referred to as the **recovery section**. The **action** component of a recovery section can not be made of more than 1 atomic part. (i.e either a time, or a distance, but not, for example `3 x 1mn`).

If the recovery section is made up of a single component, then parenthesises surrounding it are not needed. However if the recovery section has more than 1 component, then parenthesises surrounding it are required.

*Examples*

```
R=5mn
R=(5mn @5:30/km)
R=(5mn downhill @(5:00-5:10)/k)
R=downhill
```

The letter **R** can be replaced with **W** or **S**, respectively representing walked and static recoveries.

*Examples*

```
W=200m        # 200m walk recovery between each rep
S=30s         # 30s static recovery between each rep
```

> **_NOTE:_**  **Walk** and **Static** recoveries only accept a section with a **single atomic action component** as a parameter. For **Static** recoveries, this component can only be a **time**.

In addition to these different recoveries, the letter **C** can be used as a prefix to the recovery to define a **Recovery Cycle**, which will be described in a subsequent section of this document.

### Application

A recovery applies to (i.e is observed after) the main component of the section it is in. If the recovery is the main component of the section, then it is observed directly after the previous section of the workout. When the main component of the section is an **action component**, the recovery is observed after *each* section in the component. However, in most cases, recovery does **not** apply to the last section in an action component. This happens if:

- The set is followed by a section where the main component is a recovery;
- The set is followed by a section with either `warmup` or `cooldown` as its main component;
- The workout ends after the set.

*Examples*

```
WU; 4 x 4mn R=1mn ; CD
7 x 600m, CS=3mn
6 x 300m R=100m; R=5mn; 15mn @MP
```

In the 3 examples above, recovery does **not** apply to the last repetition. Let's consider the last example from above and remove the 5mn recovery section between the 2 workout sections:

```
6 x 300m R=100m; 15mn @MP
```

In this case, because the 300m reps are directly followed by another workout section, recovery for the last 300m rep is applied.

Multiple recoveries may be specified for the same section. See **Mixing Different Recoveries** for cases where this may be relevant. When doing so, the most specific recovery takes prevalence, or the one that is declared first if the two recoveries are identically specific. All recovery types are equally specific. The hierarchy of specificity is:

```
Rep Specific > Rep Group Specific > Rep Type Specific > Not Specific
```

### Recovery Cycles

Instead of a fixed recovery time or distance, it is possible to define a "recovery cycle", where the recovery is a function of each rep time or distance. In a recovery cycle, recovery is calculated as follows:

```
Recovery = Cycle - Rep
```

The syntax for declaring a cycle is to prefix recovery with the letter `C`.

*Examples*

```
CR=5mn
CW=800m
CS=3mn
```

Let's consider the following workout:

```
6 x 800m CW=5mn
```

In this example, an 800m repetition run in 3mn will lead to a 2mn walk recovery, whereas a repetition run in 3:10 will be followed by a 1:50 walk recovery. Recovery cycles are better used by mixing distance repetitions and time recoveries, or vice versa. A cycle using the same dimension for repetitions and recovery can always be re-written without a cycle. For example, `6 x 800m CR=1km` is equivalent to `6 x 800m R=200m`.

### Mixing Different Recoveries

Sometimes, specifying a single recovery is not enough, and more granularity is required. For example, a workout may mix different rep types, or have increasing or decreasing recovery time throughout the workout. Here are a few ways that can help with this.

#### Arithmetic Operations as Recovery

The **action component** of a recovery (or time / distance for **Static** and **Walk** recoveries) can reference the repetition it follows by using the `rep` keyword, which can be used as a variable to perform basic arithmetic operations on it. The scope of what is supported is limited. `rep` can only be referenced once, and it must always be referenced at the very beginning of the **action component** of the recovery segment.

`rep` can be multiplied or divided by an integer with the `x` and `/` characters, respectively. e.g `rep / 2` or `rep x 4`. Time or distance can then be added to or subtracted from it, with `+` and `-`. No more than 2 operations are allowed (a multiplication or division, followed by a multiplication or subtraction). for example, this is **not allowed**: `rep x 4 + 2mn - 7s`. To make this legal, it would have to be re-written as `rep x 4 + 1:53`. Parenthesises are mandatory around the arithmetics operation.

*Examples*

```
# 4mn static recovery
3 x 5mn S=(rep - 1mn)

# 30s + 200m, 1mn + 200m, 1mn30 + 200m jog recovery at 130 to 140 bpm
(1, 2, 3)mn R=((rep / 2 + 200m) @(130-140)bpm)

# 300m walk recovery
3 x 600m W=(rep / 2)

# for each rep, recovery is equal to rep time
(3, 3, 3, 2, 2, 1)mn, R=(rep)
```

> **_NOTE:_** This is not compatible with **Recovery Cycles**.

#### Rep-Specific Recovery 

It is possible to define a recovery that only applies to a single rep. This can be done with the following syntax:

```
{recovery-type}{rep-number}={recovery}
```

Where `{rep-number}` represents the rep this recovery applies to. The first rep in a set has number 1.

*Example*

```
5 x 1km R1=1mn R2=2mn R=3mn
```

In this example, the recovery after the first 1km run will be 1mn. The recovery after the second rep will be 2mn, and the recovery for subsequent reps will be 3mn. Note the use of `R=`, without any rep number, to mean "all remaining reps". This is making use of the recovery specificity rule described above.

#### Rep Group-Specific Recovery

Similar to the above, it is possible to specify a recovery for a group of reps, by referencing their rep-numbers:

```
{recovery-type}({rep-numbers})={recovery}
```

Where `{rep-numbers}` are comma separated numbers, or ranges of numbers referencing reps. Ranges are declared with the syntax: `{lower-bound}-{upper-bound}` (e.g `1-4`).

*Example*s

```
R(1,3,5)=1mn        # Applies to reps 1, 3, 5
R(2-4)=1mn          # Applies to reps 2, 3, 4
R(1-4,6,8)=1mn      # Applies to reps 1, 2, 3, 4, 6, 8
```

#### Rep Type-Specific Recovery

For heterogenous workouts, recovery can also be targeted at a specific type of reps in a workout:

```
{recovery-type}{rep-type}={recovery-segment}
```

Where `{rep-type}` is either a time or a distance.

*Example*

```
(1, 2, 3, 4, 5, 4, 3, 2, 1)mn R1mn=30s R2mn=1mn W(3,4)mn=2mn S5mn=2:30
```

In this example, 1mn reps will be followed by 30s of recovery, 2mn reps will be followed by 1mn, 
3 and 4mn reps will be followed by 2mn of walking and the 5mn rep will be followed by a static recovery of 2mn30s.

As with Rep-Specific recoveries, it is possible to use groups (see example above), but ranges are not permitted.

*Other examples*
```
(4 x 400m ; 6 x 300m ; 8 x 100m) W400m=200m W(300, 100)m=100m
(1, 1, 2, 2, 3, 3)km R1km=2mn S2km=3mn, R3km=4mn
```


## Time & Distance

This section lists available units and formatting for time and distance.

### Time
#### Available Time Units

| Unit | Meaning |
| ---- | ---- |
| `h` | Hour |
| `mn` or `'` | Minute |
| `s` or `"` | Second |
Fractions of a second are not currently supported.
#### Representation of Time

Time can be represented using an integer followed by one of the time units listed above.

*Examples*

```
4mn
2h
60s
1'
30"
```

These representations can be concatenated together, with the bigger unit always on the left:

```
{hh}h{mm}mn{ss}s OR {hh}h{mm}mn{ss}
{hh}h{mm}mn      OR {hh}h{mm}
{mm}mn{ss}s      OR {mm}mn{ss}
{mm}'{ss}"       OR {mm}'{ss}
```

*Examples*

```
4mn04s
12h30mn30s
12h30
3mn30
2'55"
```


In addition to this representation, time can also be written without specifying any unit. This is done with the following syntax:

```
{hh}:{mm}:{ss}
{mm}:{ss}
```

*Examples*

```
03:00        # 3mn
1:30         # 1mn30
12:00:00     # 12h
```

> **_NOTE:_**  For both representations, leading 0s are only optional for the biggest unit specified. `4mn04s` is a valid representation of time, and equivalent to `04mn04s` (and `4:04` and `04:04`), but `04:4` and `4:4` are **not** valid representations of time.

### Distance
#### Available Distance Units

| Short Version | Fully Qualified Unit |
| ------------- | -------------------- |
| `km`, `k`     | Kilometer            |
| `m`           | Meter                |
| `M`           | Mile                 |
| `yd`          | Yard                 |
#### Representation of Distance

Distance is simply represented with an integer or a floating point number followed by a distance unit.

*Examples*

```
10k
5.55km
400m
13.1M
60yd
```

Constructs such as `1km400m` are not valid. 

Rundown also allows fully qualifying available distance units (see **Available Distance Units**), in which case a space separating the distance and the unit is required. Fully qualified units are **case insensitive**.

*Examples*

```
1 Kilometer
5 Mile
300 meter
```