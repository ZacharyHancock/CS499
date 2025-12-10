
# Custom exception class
class MyException(Exception):
    def __str__(self):
        return "MY EXCEPTION OCCURED!"

# do_even_more_custom_application_logic: throws any standard exception
def do_even_more_custom_application_logic():
    print("Running Even More Custom Application Logic.")

    raise RuntimeError("Something went very very wrong!!")

#do_custom_application_logic: runs functions and throws exceptions
def do_custom_application_logic():
    print("Running Custom Application Logic.")

    #try to run do_even_more_custom_application_logic, catch exception
    try:
        do_even_more_custom_application_logic()
    except Exception as ex:
        print("Exception caught!!", str(ex))

    raise MyException()

#inputs: num - dividend, den - divisor
#divide: divides by zero to raise exception
def divide(num, den):
    if den == 0:
        raise ZeroDivisionError("Divide by zero occurred!!!!!")
    return num / den

# do_division: runs divide function and catches exception
def do_division():
    numerator = 10.0
    denominator = 0

    # try to run divide function, catch ZeroDivisionError
    try:
        result = divide(numerator, denominator)
        print(f"divide({numerator}, {denominator}) = {result}")
    except ZeroDivisionError as ex:
        print("Divide by zero occurred:", str(ex))

def main():
    print("Exceptions Tests!")

    # try to run do functions, catch exceptions in order
    try:
        do_division()
        do_custom_application_logic()
    except MyException as ex:
        print("Caught my custom exception:", str(ex))
    except Exception as ex:
        print("Caught standard exception:", str(ex))
    except BaseException:
        print("Caught all unknown exceptions")

if __name__ == '__main__':
    main()