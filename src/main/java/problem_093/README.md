# Project Euler problem 093

## Problem description

> By using each of the digits from the set, {1, 2, 3, 4}, exactly once, and making use of the four arithmetic
> operations (+, −, *, /) and brackets/parentheses, it is possible to form different positive integer targets.
>
> For example,
>
> 8 = (4 * (1 + 3)) / 2
> 14 = 4 * (3 + 1 / 2)
> 19 = 4 * (2 + 3) − 1
> 36 = 3 * 4 * (2 + 1)
>
> Note that concatenations of the digits, like 12 + 34, are not allowed.
>
> Using the set, {1, 2, 3, 4}, it is possible to obtain thirty-one different target numbers of which 36 is the maximum,
> and each of the numbers 1 to 28 can be obtained before encountering the first non-expressible number.
>
> Find the set of four distinct digits, a < b < c < d, for which the longest set of consecutive positive integers, 1 to n,
> can be obtained, giving your answer as a string: abcd.

### Analysis

By brute force:

There are 10C4 = 210 possible combinations of strings 'abcd' that we can build with the digits [0-9]
For each string we can permute it 4! = 24 different ways
Using the Catalan numbers, we know there are 5 distinct ways of grouping {a,b,c,d}:

1. ((ab)c)d
2. (a(bc))d
3. (ab)(cd)
4. a((bc)d)
5. a(b(cd))

and each must be evaluated because PEMDAS does not remove all ambiguity from the order of operations.
Finally, there are 4^3 = 64 ways to choose the three binary operators that go between the four digits

Total there is (10C4)(4!)(C_4)(4^3) = (210)(24)(5)(64) = 1,612,800 possibilities that we need to check. However a
significant percentage will be redundant calculations.

### Findings

Using four digits and the 4 basic operations of add, subtract, multiply & divide. There can be at most 1170 unique
values formed. For two numbers, **a** & **b**, it is easy to see there are 6 unique values:

**a+b, a-b, b-a, b/a, a/b, a*b**

In general, the number of inequivalent expressions involving **n** operands from the set **{+,-,*,/}**
Can be given by the integer sequence [OEIS A140606](https://oeis.org/A140606).