#!/usr/bin/python
from sympy.solvers import solve
from sympy import Symbol

with open("data/polynomial.txt") as fp:
    poly = fp.read()

x = Symbol('x')
solution = solve(poly, x)

with open("data/polynomial.txt", "w") as fp:
    fp.write(str(solution[0]))
