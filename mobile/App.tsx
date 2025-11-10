import React, { useEffect, useState } from 'react'
import { Text, View, FlatList } from 'react-native'

type Job = { id: number; title: string; price: number }

export default function App() {
  const [jobs, setJobs] = useState<Job[] | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    fetch('http://10.0.2.2:8080/jobs') // emulator localhost mapping
      .then((r) => r.json())
      .then(setJobs)
      .catch((e) => setError(String(e)))
  }, [])

  return (
    <View style={{flex:1, padding:20, backgroundColor:'#fff'}}>
      <Text style={{fontSize:24, fontWeight:'700'}}>Dustbusters of Destin</Text>
      {error && <Text style={{color:'red'}}>{error}</Text>}
      {!jobs && !error && <Text>Loading...</Text>}
      {jobs && (
        <FlatList
          data={jobs}
          keyExtractor={(item) => String(item.id)}
          renderItem={({item}) => (
            <Text style={{paddingVertical:8}}>{item.title} — ${item.price}</Text>
          )}
        />
      )}
    </View>
  )
}
