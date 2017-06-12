# Image retrieval tool writter for MIR lecture

## Usage
You can use java -jar to execute the program. Arguments are evaluated as follows:
* 1st argument: path of the directory that is indexed
* 2nd argument: one picture to search
* 3rd argument: one picture to search

The features that are extracted are stored next to the image file in a file that has .features as an file-extension

## How it works
* jpg, png, bmp and gif images can be indexed
* features that have not yet been extracted are read from file, if the .features file does not exist they are computed and saved
* an image can be queried (either by entering the path or by command line argument)
* the 5 best fitting images and the worst fitting image are displayed

## Similarity Measures

### Histogram
* The histogram is computed using 10 buckets for each value H, S and V for each pixel
* When the similarity / euler distance is computed the vectors are normalised

### Matrix Distance
* I wanted to try a second similarity measure not based on colors so I invented this one
* The image is scaled 200 * 200 pixels in size and converted to grayscale
* Afterwards the image is decomposed using Singular Value Decomposition and the rank is reduced to 10 (to further minimise memory usage)
* Similarity is computed on the low-rank-approximation of the matrix as the Frobenius-Norm of A'-B' (where A' is U * S * Vt in their low-rank form)
* In hindsight: this may be a case of 'if you have a hammer everything looks like a nail'-svd usage ;)

### Combined Similarity
* The two similarity measures need to be computed into a set of solutions
* My initial plan was to compute the pareto-optimal set (which wasn't implemented due to time)
* The actual similarity used is cosine-similarity for the histogram and 1 / (d + 1) as the matrix-similarity i
* The two similarities are multiplied (which eliminates the need to normalise the values, matrix-similrity yields much smaller values then histogram-similarity)
* As the matrix-similarity/distance already contains brightness information the brightness was excluded from the histogram similarity to make it not over-important
