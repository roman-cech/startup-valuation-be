package cz.utb.fai.redis

class Pair<A, B>(private val jobStatus: A, private val response: B){
    override fun toString(): String {
        return "($jobStatus, $response)"
    }
}