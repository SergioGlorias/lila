package lila.fide

import monocle.syntax.all.*
import reactivemongo.api.bson.Macros.Annotations.Key
import chess.FideId

case class Federation(
    @Key("_id") id: Federation.Id,
    name: Federation.Name,
    nbPlayers: Int,
    standard: Federation.Stats,
    rapid: Federation.Stats,
    blitz: Federation.Stats,
    updatedAt: Instant
):
  def stats(tc: FideTC) = tc match
    case FideTC.standard => this.focus(_.standard)
    case FideTC.rapid    => this.focus(_.rapid)
    case FideTC.blitz    => this.focus(_.blitz)
  def slug = Federation.nameToSlug(name)

// Obviously, FIDE country codes don't follow any existing standard.
// https://ratings.fide.com/top_federations.phtml
// all=[];$('#federations_table').find('tbody tr').each(function(){all.push([$(this).find('img').attr('src').slice(5,8),$(this).find('a,strong').text().trim()])})
object Federation:

  type Id   = String
  type Name = String

  case class Stats(rank: Int, nbPlayers: Int, top10Rating: Int)

  type ByFideIds = Map[FideId, Id]

  export FidePlayer.nameToSlug

  def idToSlug(id: Id): String = nameToSlug(name(id))

  def name(id: Id): Name = names.getOrElse(id, id)

  def find(str: String): Option[Id] =
    names.contains(str.toUpperCase).option(str.toUpperCase) orElse
      bySlug.get(str).orElse(bySlug.get(nameToSlug(str)))

  lazy val bySlug: Map[String, Id] = names
    .map: (id, name) =>
      nameToSlug(name) -> id

  val names: Map[Id, Name] = Map(
    "FID" -> "FIDE"
    "USA" -> "United States of America",
    "IND" -> "India",
    "CHN" -> "China",
    "RUS" -> "Russia",
    "AZE" -> "Azerbaijan",
    "FRA" -> "France",
    "UKR" -> "Ukraine",
    "ARM" -> "Armenia",
    "GER" -> "Germany",
    "ESP" -> "Spain",
    "NED" -> "Netherlands",
    "HUN" -> "Hungary",
    "POL" -> "Poland",
    "ENG" -> "England",
    "ROU" -> "Romania",
    "NOR" -> "Norway",
    "UZB" -> "Uzbekistan",
    "ISR" -> "Israel",
    "CZE" -> "Czech Republic",
    "SRB" -> "Serbia",
    "CRO" -> "Croatia",
    "GRE" -> "Greece",
    "IRI" -> "Iran",
    "TUR" -> "Turkiye",
    "SLO" -> "Slovenia",
    "ARG" -> "Argentina",
    "SWE" -> "Sweden",
    "GEO" -> "Georgia",
    "ITA" -> "Italy",
    "CUB" -> "Cuba",
    "AUT" -> "Austria",
    "PER" -> "Peru",
    "BUL" -> "Bulgaria",
    "BRA" -> "Brazil",
    "DEN" -> "Denmark",
    "SUI" -> "Switzerland",
    "CAN" -> "Canada",
    "SVK" -> "Slovakia",
    "LTU" -> "Lithuania",
    "VIE" -> "Vietnam",
    "AUS" -> "Australia",
    "BEL" -> "Belgium",
    "MNE" -> "Montenegro",
    "MDA" -> "Moldova",
    "KAZ" -> "Kazakhstan",
    "ISL" -> "Iceland",
    "COL" -> "Colombia",
    "BIH" -> "Bosnia & Herzegovina",
    "EGY" -> "Egypt",
    "FIN" -> "Finland",
    "MGL" -> "Mongolia",
    "PHI" -> "Philippines",
    "BLR" -> "Belarus",
    "LAT" -> "Latvia",
    "POR" -> "Portugal",
    "CHI" -> "Chile",
    "MEX" -> "Mexico",
    "MKD" -> "North Macedonia",
    "INA" -> "Indonesia",
    "PAR" -> "Paraguay",
    "EST" -> "Estonia",
    "SGP" -> "Singapore",
    "SCO" -> "Scotland",
    "VEN" -> "Venezuela",
    "IRL" -> "Ireland",
    "URU" -> "Uruguay",
    "TKM" -> "Turkmenistan",
    "MAR" -> "Morocco",
    "MAS" -> "Malaysia",
    "BAN" -> "Bangladesh",
    "ALG" -> "Algeria",
    "RSA" -> "South Africa",
    "AND" -> "Andorra",
    "ALB" -> "Albania",
    "KGZ" -> "Kyrgyzstan",
    "KOS" -> "Kosovo *",
    "FAI" -> "Faroe Islands",
    "ZAM" -> "Zambia",
    "MYA" -> "Myanmar",
    "NZL" -> "New Zealand",
    "ECU" -> "Ecuador",
    "CRC" -> "Costa Rica",
    "NGR" -> "Nigeria",
    "JPN" -> "Japan",
    "SYR" -> "Syria",
    "DOM" -> "Dominican Republic",
    "LUX" -> "Luxembourg",
    "WLS" -> "Wales",
    "BOL" -> "Bolivia",
    "TUN" -> "Tunisia",
    "UAE" -> "United Arab Emirates",
    "MNC" -> "Monaco",
    "TJK" -> "Tajikistan",
    "PAN" -> "Panama",
    "LBN" -> "Lebanon",
    "NCA" -> "Nicaragua",
    "ESA" -> "El Salvador",
    "ANG" -> "Angola",
    "TTO" -> "Trinidad & Tobago",
    "SRI" -> "Sri Lanka",
    "IRQ" -> "Iraq",
    "JOR" -> "Jordan",
    "UGA" -> "Uganda",
    "MAD" -> "Madagascar",
    "ZIM" -> "Zimbabwe",
    "MLT" -> "Malta",
    "SUD" -> "Sudan",
    "KOR" -> "South Korea",
    "PUR" -> "Puerto Rico",
    "HON" -> "Honduras",
    "GUA" -> "Guatemala",
    "PAK" -> "Pakistan",
    "JAM" -> "Jamaica",
    "THA" -> "Thailand",
    "YEM" -> "Yemen",
    "LBA" -> "Libya",
    "CYP" -> "Cyprus",
    "NEP" -> "Nepal",
    "HKG" -> "Hong Kong, China",
    "SSD" -> "South Sudan",
    "BOT" -> "Botswana",
    "PLE" -> "Palestine",
    "KEN" -> "Kenya",
    "AHO" -> "Netherlands Antilles",
    "MAW" -> "Malawi",
    "LIE" -> "Liechtenstein",
    "TPE" -> "Chinese Taipei",
    "AFG" -> "Afghanistan",
    "MOZ" -> "Mozambique",
    "KSA" -> "Saudi Arabia",
    "BAR" -> "Barbados",
    "NAM" -> "Namibia",
    "HAI" -> "Haiti",
    "ARU" -> "Aruba",
    "CIV" -> "Cote d’Ivoire",
    "CPV" -> "Cape Verde",
    "SUR" -> "Suriname",
    "LBR" -> "Liberia",
    "IOM" -> "Isle of Man",
    "MTN" -> "Mauritania",
    "BRN" -> "Bahrain",
    "GHA" -> "Ghana",
    "OMA" -> "Oman",
    "BRU" -> "Brunei Darussalam",
    "GCI" -> "Guernsey",
    "GUM" -> "Guam",
    "KUW" -> "Kuwait",
    "JCI" -> "Jersey",
    "MRI" -> "Mauritius",
    "SEN" -> "Senegal",
    "BAH" -> "Bahamas",
    "MDV" -> "Maldives",
    "NRU" -> "Nauru",
    "TOG" -> "Togo",
    "FIJ" -> "Fiji",
    "PLW" -> "Palau",
    "GUY" -> "Guyana",
    "LES" -> "Lesotho",
    "CAY" -> "Cayman Islands",
    "SOM" -> "Somalia",
    "SWZ" -> "Eswatini",
    "TAN" -> "Tanzania",
    "LCA" -> "Saint Lucia",
    "ISV" -> "US Virgin Islands",
    "SLE" -> "Sierra Leone",
    "BER" -> "Bermuda",
    "SMR" -> "San Marino",
    "BDI" -> "Burundi",
    "QAT" -> "Qatar",
    "ETH" -> "Ethiopia",
    "DJI" -> "Djibouti",
    "SEY" -> "Seychelles",
    "PNG" -> "Papua New Guinea",
    "DMA" -> "Dominica",
    "STP" -> "Sao Tome and Principe",
    "MAC" -> "Macau",
    "CAM" -> "Cambodia",
    "VIN" -> "Saint Vincent and the Grenadines",
    "BUR" -> "Burkina Faso",
    "COM" -> "Comoros Islands",
    "GAB" -> "Gabon",
    "RWA" -> "Rwanda",
    "CMR" -> "Cameroon",
    "MLI" -> "Mali",
    "ANT" -> "Antigua and Barbuda",
    "CHA" -> "Chad",
    "GAM" -> "Gambia",
    "COD" -> "Democratic Republic of the Congo",
    "SKN" -> "Saint Kitts and Nevis",
    "BHU" -> "Bhutan",
    "NIG" -> "Niger",
    "GRN" -> "Grenada",
    "BIZ" -> "Belize",
    "CAF" -> "Central African Republic",
    "ERI" -> "Eritrea",
    "GEQ" -> "Equatorial Guinea",
    "IVB" -> "British Virgin Islands",
    "LAO" -> "Laos",
    "SOL" -> "Solomon Islands",
    "TGA" -> "Tonga",
    "TLS" -> "Timor-Leste",
    "VAN" -> "Vanuatu"
  )
