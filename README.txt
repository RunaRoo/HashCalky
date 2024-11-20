Simple Cli Java MD5 Hash Calculator to Verify File Integrity

Usage
Run java -jar HashCalky.jar with the following arguments:

Single File Hashing
Run the command:

sh
java -jar HashCalky.jar generatehash <filepath>
This calculates the MD5 hash of the specified file and writes the hash to a file with the same name and a .md5 extension.

Example file output:

TestText.txt md5 = a0e1d3c9ad48dabf57becabe3e654d05
Multiple File Hashing (Directory)
Run the command:

sh
java -jar HashCalky.jar generatehash <filepath> buildpaths
This calculates MD5 hashes for all regular files within the specified directory and its subdirectories. 
The results are written to a file named <filepath>_hashes.txt. Each line contains the file path followed by its MD5 hash.

Example file output:

TestDirectory\TestDir1.pdf md5 = ecbf406fb0b5f3f7bec313bfe52a0bae
TestDirectory\TestFile2.txt md5 = b8b3f8f8972f00a5f52b2e229d173ab1
TestDocument1.odt md5 = 409cfdf51075c2eb26889453255009cb
TestFile2.mp4 md5 = 6a96503c5e54a9b8d8d839c27dae2985
TestText.txt md5 = a0e1d3c9ad48dabf57becabe3e654d05
Hash Verification
Create a file containing expected MD5 hashes for your files in the format file_path md5 = <expected_hash>.

Run the command:

sh
java -jar HashCalky.jar checkhash <hashfilepath>
This compares the expected hashes in the provided file with the calculated hashes for the corresponding files. 
The results are written to a file named <hashfilepath>_results.txt. Each line indicates whether the calculated 
hash matches the expected hash for the file.

Example file output:

TestDirectory\TestDir1.pdf md5 = ecbf406fb0b5f3f7bec313bfe52a0bae calcedmd5 = ecbf406fb0b5f3f7bec313bfe52a0bae true
TestDirectory\TestFile2.txt md5 = b8b3f8f8972f00a5f52b2e229d173ab1 calcedmd5 = 41b757db1c2bd974c3ed8f5042584700 false
TestDocument1.odt md5 = 409cfdf51075c2eb26889453255009cb calcedmd5 = 409cfdf51075c2eb26889453255009cb true
TestFile2.mp4 md5 = 6a96503c5e54a9b8d8d839c27dae2985 calcedmd5 = 6a96503c5e54a9b8d8d839c27dae2985 true
TestText.txt md5 = a0e1d3c9ad48dabf57becabe3e654d05 calcedmd5 = ca1571d4f5da794587ab97dd92a8f8e7 false
A second file named erroredfiles.txt will be generated with the file names that did not pass MD5 verification.

Example file output:

TestDirectory\TestFile2.txt
TestText.txt
