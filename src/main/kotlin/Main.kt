import time.UniformDurationGenerator

fun main(args: Array<String>) {
    val x = UniformDurationGenerator(5, 10)
    println(x.generate())
    println(x.generate())
}