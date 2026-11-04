import { before, after, beforeEach, test } from 'node:test';
import { readFile } from 'node:fs/promises';
import { initializeTestEnvironment, assertSucceeds, assertFails } from '@firebase/rules-unit-testing';
import { doc, setDoc, getDoc, deleteDoc, collection, getDocs } from 'firebase/firestore';

let environment;
const draft = {
  id: 'draft-1', category: 'Food / Essentials', helpingSomeoneElse: false,
  name: '', phone: '', location: '', details: '', createdAtMillis: Date.now()
};
const ref = (db, user = 'alice') => doc(db, `users/${user}/intakes/draft-1`);

before(async () => {
  environment = await initializeTestEnvironment({
    projectId: 'demo-wecareto',
    firestore: { rules: await readFile(new URL('../firestore.rules', import.meta.url), 'utf8') }
  });
});
beforeEach(async () => { await environment.clearFirestore(); });
after(async () => { await environment?.cleanup(); });

test('an anonymous Firebase identity can save and retrieve only its own intake', async () => {
  const alice = environment.authenticatedContext('alice', { firebase: { sign_in_provider: 'anonymous' } }).firestore();
  await assertSucceeds(setDoc(ref(alice), draft));
  await assertSucceeds(getDoc(ref(alice)));
  await assertSucceeds(getDocs(collection(alice, 'users/alice/intakes')));
  const bob = environment.authenticatedContext('bob').firestore();
  await assertFails(getDoc(ref(bob)));
  await assertFails(setDoc(ref(bob), draft));
  await assertFails(deleteDoc(ref(bob)));
  await assertFails(getDocs(collection(bob, 'users/alice/intakes')));
});

test('unauthenticated clients cannot read or write intakes', async () => {
  const db = environment.unauthenticatedContext().firestore();
  await assertFails(setDoc(ref(db), draft));
  await assertFails(getDoc(ref(db)));
});

test('a retry is idempotent and accepted answers cannot be silently changed', async () => {
  const db = environment.authenticatedContext('alice').firestore();
  await assertSucceeds(setDoc(ref(db), draft));
  await assertSucceeds(setDoc(ref(db), draft));
  await assertFails(setDoc(ref(db), { ...draft, name: 'Changed' }));
  await assertSucceeds(deleteDoc(ref(db)));
});

test('rules reject unexpected fields, mismatched IDs, invalid types and oversized input', async () => {
  const db = environment.authenticatedContext('alice').firestore();
  for (const bad of [
    { ...draft, extra: 'not allowed' }, { ...draft, id: 'different' },
    { ...draft, category: 'unknown' }, { ...draft, helpingSomeoneElse: 'yes' },
    { ...draft, name: 'x'.repeat(101) }, { ...draft, details: 'x'.repeat(2001) },
    { ...draft, location: 'x'.repeat(201) }, { ...draft, phone: '123' },
    { ...draft, phone: 'letters4165550100' }, { ...draft, createdAtMillis: 0 }
  ]) await assertFails(setDoc(ref(db), bad));
  await assertSucceeds(setDoc(ref(db), { ...draft, phone: '(416) 555-0100' }));
});
