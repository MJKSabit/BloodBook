import { Link } from "@material-ui/core"
import axios from "axios"
import { useEffect } from "react"
import { useState } from "react"

const LocationViewer = ({location}) => {
  const [placename, setPlacename] = useState(null)

  useEffect(() => {
    axios.get(`https://api.bigdatacloud.net/data/reverse-geocode-client?localityLanguage=en&latitude=${location.latitude}&longitude=${location.longitude}`).then(
      response => {
        const area = `${response.data.locality}, ${response.data.principalSubdivision}, ${response.data.countryName}`
        setPlacename(area)
      }
    )
  }, [location])

  const text = placename ? `${placename} ↗` : `${location.latitude}, ${location.longitude} ↗`

  return <Link
    href={`https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}`}
    rel="noopener noreferrer" 
    target="_blank">
      {text}
  </Link>
} 

export default LocationViewer