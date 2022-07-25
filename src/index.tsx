import { NativeModules, Platform , NativeEventEmitter　} from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-ironsourceads' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const Ironsourceads = NativeModules.Ironsourceads
  ? NativeModules.Ironsourceads
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

const emitter = new NativeEventEmitter(Ironsourceads);
const subScriptions = {};

const addEventListener = (event, handler) => {
  let subScription = emitter.addListener(event, handler);
  let currentSubscription = subScriptions[event];
  if (currentSubscription) {
    currentSubscription.remove();
  }
  subScriptions[event] = subScription;
};

const removeEventListener = (event) => {
  let currentSubscription = subScriptions[event];
  if (currentSubscription) {
    currentSubscription.remove();
    delete subScriptions[event];
  }
};

export default {
  ...Ironsourceads,
  addEventListener,
  removeEventListener,
  // Use callback to avoid need for attaching listeners at top level on each re-render
  initialize(sdkKey, callback) {
    Ironsourceads.initialize(sdkKey, callback); 
  },

};
