# This is a sample Python script.
import sys


# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.


def add_numbers(start, increment, steps):
    result = start
    for i in range(steps):
        if result > (sys.maxsize - increment):
            return None
        else:
            result += increment
    return result

def subtract_numbers(start, decrement, steps):
    result = start
    for i in range(steps):
        if result < (-sys.maxsize + decrement):
            return None
        else:
            result -= decrement
    return result

def test_overflow():
    steps = 5

    increment = sys.maxsize / steps

    start = 0

    print("Adding Numbers Without Overflow", start,", ", increment, ", ", steps)
    if

# Press the green button in the gutter to run the script.
if __name__ == '__main__':


# See PyCharm help at https://www.jetbrains.com/help/pycharm/
