// Exceptions.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include <iostream>

// Custom exception class
class MyException : public std::exception {
public:
    const char* what() const noexcept override {
        return "MY EXCEPTION OCCURED!";
    }
};

bool do_even_more_custom_application_logic()
{
    // TODO: Throw any standard exception

    std::cout << "Running Even More Custom Application Logic." << std::endl;

    //standard exception thrown
    throw std::runtime_error("Something went very very wrong!!");

    return true;
}

void do_custom_application_logic()
{
    // TODO: Wrap the call to do_even_more_custom_application_logic()
    //  with an exception handler that catches std::exception, displays
    //  a message and the exception.what(), then continues processing
    std::cout << "Running Custom Application Logic." << std::endl;

    try { // try to call function
        if (do_even_more_custom_application_logic())
        {
            std::cout << "Even More Custom Application Logic Succeeded." << std::endl;
        }
    }
    catch (const std::exception& ex) { // catch exception
        std::cout << "Exception caught!!" << ex.what() << std::endl;
    }

    // TODO: Throw a custom exception derived from std::exception
    //  and catch it explictly in main
    throw MyException(); // throw my custom exception

    std::cout << "Leaving Custom Application Logic." << std::endl;

}

float divide(float num, float den)
{
    // TODO: Throw an exception to deal with divide by zero errors using a standard C++ defined exception
    if (den == 0) { // throw overflow_error if divide by zero is possible
        throw std::overflow_error("Divide by zero occured!!!!!");
    }
    return (num / den);
}

void do_division() noexcept
{
    //  TODO: create an exception handler to capture ONLY the exception thrown
    //  by divide.

    float numerator = 10.0f;
    float denominator = 0;

    try { // try to divide
        auto result = divide(numerator, denominator);
        std::cout << "divide(" << numerator << ", " << denominator << ") = " << result << std::endl;
    } // catch exception for divide by zero
    catch (const std::overflow_error& ex) {
        std::cout << "Divide by zero occured:" << ex.what() << std::endl;
    }
    
}

int main()
{
    std::cout << "Exceptions Tests!" << std::endl;

    // TODO: Create exception handlers that catch (in this order):
    //  your custom exception
    //  std::exception
    //  uncaught exception 
    //  that wraps the whole main function, and displays a message to the console.

    try {
        do_division();
        do_custom_application_logic();
    }
    catch (const MyException& ex) { // catch MyException custom exception
        std::cout << "Caught my custom exception: " << ex.what() << std::endl;
    }
    catch (const std::exception& ex) { // catch std::exception
        std::cout << "Caught my std::exception: " << ex.what() << std::endl;
    }
    catch (...) { // catch all handler
        std::cout << "Caught all unknown exceptions: " << std::endl;
    }
    
}


// Run program: Ctrl + F5 or Debug > Start Without Debugging menu
// Debug program: F5 or Debug > Start Debugging menu