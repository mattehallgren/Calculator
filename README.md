# Calculator

syntax(System.in): <register> <operation> <value>
example: A add 10


This is a calculator with lazy evaluation(evaluated at print),

for example, you're not forced to do the operations in a typical descending order, you can mix it up like this:

result add revenue
result subtract costs
revenue add 200
costs add salaries
salaries add 20
salaries multiply 5
costs add 10
print result
QUIT
The output should be:
90 
