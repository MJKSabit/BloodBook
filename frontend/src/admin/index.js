import { Route, Switch } from "react-router-dom";
import ImageUploader from "../generic/ImageUploader";
import LocationSelector from "../generic/LocationSelector";

export default function Admin() {
  return (
    <ImageUploader onUpload={url => console.log(url)}/>
  )
}