// BufferOverflow.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include <iomanip>
#include <iostream>


int main()
{
	std::cout << "Buffer Overflow Example" << std::endl;

	// TODO: The user can type more than 20 characters and overflow the buffer, resulting in account_number being replaced -
	//  even though it is a constant and the compiler buffer overflow checks are on.
	//  You need to modify this method to prevent buffer overflow without changing the account_number
	//  variable, and its position in the declaration. It must always be directly before the variable used for input.
	//  You must notify the user if they entered too much data.

	const std::string account_number = "CharlieBrown42";
	char user_input[20];
	std::cout << "Enter a value: ";
	std::cin.get(user_input, sizeof(user_input)); // gets the input from the buffer with the size of user_input, truncated to sizeOf user_input if its longer
	 
	if (std::cin.peek() != '\n') { // checks if there is a \n at the end of the input, since it would be there if the input fits within the buffer size, and wouldnt be there if its the size of user_input
		std::cout << "Warning: you entered too many characters! Input truncated to size of user_input." << std::endl; // notify the user of buffer overflow
	}

	std::cout << "You entered: " << user_input << std::endl;
	std::cout << "Account Number = " << account_number << std::endl;
}

// Run program: Ctrl + F5 or Debug > Start Without Debugging menu
// Debug program: F5 or Debug > Start Debugging menu
