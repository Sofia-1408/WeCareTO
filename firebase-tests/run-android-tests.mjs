import { spawnSync } from 'node:child_process';
import { resolve } from 'node:path';

const sdk = process.env.ANDROID_HOME || process.env.ANDROID_SDK_ROOT;
if (!sdk) throw new Error('Set ANDROID_HOME to your Android SDK folder.');
const adb = resolve(sdk, 'platform-tools/adb');
const mappings = spawnSync(adb, ['reverse', '--list'], { encoding: 'utf8' });
if (mappings.status !== 0) throw new Error('Start an Android emulator before running this test.');
const added = [];
try {
  for (const port of [9099, 8080]) {
    const endpoint = `tcp:${port}`;
    const existing = mappings.stdout.split('\n').find(line => line.split(/\s+/)[1] === endpoint);
    if (existing && existing.split(/\s+/)[2] !== endpoint) {
      throw new Error(`Port ${port} already has a different forwarding rule.`);
    }
    if (!existing) {
      const result = spawnSync(adb, ['reverse', endpoint, endpoint], { stdio: 'inherit' });
      if (result.status !== 0) throw new Error(`Could not forward emulator port ${port}.`);
      added.push(endpoint);
    }
  }
  const result = spawnSync('bash', ['gradlew', ':app:connectedDebugAndroidTest',
    '-Pandroid.testInstrumentationRunnerArguments.firebaseEmulator=true',
    '-Pandroid.testInstrumentationRunnerArguments.firebaseHost=127.0.0.1', '--console=plain'],
    { cwd: new URL('..', import.meta.url), stdio: 'inherit' });
  process.exitCode = result.status ?? 1;
} finally {
  for (const endpoint of added) spawnSync(adb, ['reverse', '--remove', endpoint], { stdio: 'inherit' });
}
