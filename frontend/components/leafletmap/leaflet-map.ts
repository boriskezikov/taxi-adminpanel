import * as L from 'leaflet';
import {customElement, html, LitElement, PropertyValues} from 'lit-element';
import {nothing} from 'lit-html';

const openStreetMapLayer = 'https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png';
const openStreetMapAttribution = `&copy; <a href='https://www.openstreetmap.org/copyright'>OpenStreetMap</a> contributors`;

// class mapDto{
//     get fullAddress(): string {
//         return this._fullAddress;
//     }
//     get lng(): number {
//         return this._lng;
//     }
//     get lat(): number {
//         return this._lat;
//     }
//     private readonly _lat: number;
//     private readonly _lng: number;
//     private readonly _fullAddress: string;
//     constructor(lat: number, lng: number, fullAddress: string) {
//         this._lat = lat;
//         this._lng = lng;
//         this._fullAddress = fullAddress;
//
//     }
// }
@customElement('leaflet-map')
export class LeafletMap extends LitElement {
  private map!: L.Map;
  private greenIcon = L.icon({
        iconUrl: 'E:\\taxi-adminpanel\\frontend\\components\\leafletmap\\marker.png',

        iconSize:     [38, 95], // size of the icon
        iconAnchor:   [22, 94], // point of the icon which will correspond to marker's location
        popupAnchor:  [-3, -76] // point from which the popup should open relative to the iconAnchor
        });

  createRenderRoot() {
    // Do not use a shadow root
    return this;
  }

  render() {
    return html`${nothing}`;
  }

  firstUpdated(_changedProperties: PropertyValues) {
    super.firstUpdated(_changedProperties);

    this.map = L.map(this);
    let tileLayer = L.tileLayer(openStreetMapLayer, { attribution: openStreetMapAttribution, maxZoom: 35 });
    tileLayer.addTo(this.map);
  }

  async setView(latitude: number, longitude: number, zoomLevel: number) {
    await this.updateComplete; // Make sure map has been initialized
    this.map.setView([latitude, longitude], zoomLevel);
  }

  async setPoint(latitude: number, longitude: number, popup:string){
      await this.updateComplete;
      L.marker([latitude, longitude], {icon: this.greenIcon}).addTo(this.map).bindPopup(popup);
      await this.updateComplete;
  }

  async setPoints(dto:string){
        await this.updateComplete;
        debugger
        const usersJson: any[] = Array.of(dto)
        usersJson.forEach((address) => {
            L.marker([address.lat, address.lng], {icon: this.greenIcon})
                .addTo(this.map).bindPopup(address.fullAddress);
        })
    }
}
