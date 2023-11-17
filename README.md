# FunctionSectionAddon

Just a test for calling Skript functions via sections.
Kinda an idea of what named arguments could be like.
```vb
function calculate_sum(first value: number, second value: number) :: integer:
  return {_first value} + {_second value}
  
function meow(sassiness: number, number of meows: integer = 5):
  broadcast {_number of meows}

on script load:
  execute function calculate_sum:
    first value: 3
    second value: 4
  broadcast last return value
  execute function meow:
    sassiness: 3
    number of meows: 6
  execute function meow:
    sassiness: 3
```