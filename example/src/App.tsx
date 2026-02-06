import RNUpdater from '@pigeonmal/react-native-updater';
import { useEffect, useState } from 'react';
import { StyleSheet, Text, View } from 'react-native';
export default function App() {
  const [version, setVersion] = useState(0);
  useEffect(() => {
    RNUpdater.getVersionCode().then(setVersion);
  }, []);
  return (
    <View style={styles.container}>
      <Text>Result: {version}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
});
