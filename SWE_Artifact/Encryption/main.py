import datetime
import os

#inputs: source string - input string, key string - key for XOR encryption,
#output: string of encrypted or decrypted input string
#encrypt_decrypt: XOR encryption decryption using input file and key
def encrypt_decrypt(source: str, key: str) -> str:
    output_chars = []

    key_len = len(key)
    for i, char in enumerate(source):
        #XOR encryption (converts charts to ints, then back)
        encrypted_ord = ord(char) ^ ord(key[i % key_len])
        output_chars.append(chr(encrypted_ord))

    return "".join(output_chars)

#input: filename string - string of input file
#output: string output string of input file
#read_file: reads entire input file into a string
def read_file(filename: str) -> str:
    default_text = "John Q. Smith\nThis is my test string"

    if not os.path.exists(filename):
        return default_text

    with open(filename, "r", encoding="utf-8", errors="ignore") as f:
        return f.read()

#input: data string - source string
#output: string student name
#get_student_name: extracts student name from first line of input string
def get_student_name(data: str) -> str:
    pos = data.find("\n")
    if pos != -1:
        return data[:pos]
    return data


#input: filename string - input file string, student_name string - students name, key string - encrypyt key, data string - encrypted string
#save_data_file: saves formatted file with student name, timestamp, key used, and data
def save_data_file(filename: str, student_name: str, key: str, data: str):
    today = datetime.date.today().strftime("%Y=%m-%d")

    with open(filename, "w", encoding="utf-8") as f:
        f.write(student_name + "\n")
        f.write(today + "\n")
        f.write(key + "\n")
        f.write(data)


def main():
    print("Encryption Decryption Test!")

    input_file = "inputdatafile.txt"
    encrypted_file = "encrypteddatafile.txt"
    decrypted_file = "decrypteddatafile.txt"
    key = "password"

    # Read source
    source_string = read_file(input_file)

    student_name = get_student_name(source_string)

    #Encryption
    encrypted_string = encrypt_decrypt(source_string, key)
    save_data_file(encrypted_file, student_name, key, encrypted_string)

    #Decryption
    decrypted_string = encrypt_decrypt(encrypted_string, key)
    save_data_file(decrypted_file, student_name, key, decrypted_string)

    print(f"Read File: {input_file} - Encrypted to: {encrypted_file} - Decrypted to: {decrypted_file}")


if __name__ == '__main__':
    main()


