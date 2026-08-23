import type { CapacitorConfig } from '@capacitor/cli'
const config:CapacitorConfig={appId:'com.obysoft.faithos',appName:'FaithOS',webDir:'dist',server:{hostname:'app.faithos.local',androidScheme:'https',iosScheme:'capacitor',cleartext:false},android:{allowMixedContent:false},ios:{contentInset:'automatic'}}
export default config
