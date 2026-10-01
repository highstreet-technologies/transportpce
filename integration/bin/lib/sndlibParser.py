"""Parser for native SNDlib network topology XML files (sndlib.zib.de schema).

Converts an original SNDlib network spec (e.g. nobel-germany.xml) into the
node/link dictionaries consumed by createNTSdevices.py, so device models for
the NTS simulators can be generated directly from the SNDlib file without
pre-converted Nodes/Links JSON files.

Node dictionary format (same as Nodes_Germany_17.json):
    {"<idx>": ["<nodeId>", <x=lon>, <y=lat>, <num_of_IXPs>, <num_of_DCs>]}

Link dictionary format (same as Links_Germany_17.json):
    {"<idx>": {"linkNo": <idx>, "startNode": <id>, "endNode": <id>,
               "linkDist": <km>, "noChannels": 0, "noSpans": <n>,
               "spanList": [{"LinkName": ..., "FiberType": ...,
                             "SpanLength": ..., "EDFAGain": ..., "attnDB": ...}]}}

SNDlib files only contain geographical coordinates and per-link capacity
modules. Link distances are computed from the coordinates (haversine, km);
the span list is generated synthetically with spans of at most
maxSpanLengthKm (equal split of the link distance). Note that linkDist,
noChannels and spanList are only carried along as topology-graph attributes -
they do not influence the generated device XMLs.
"""

import math
import xml.etree.ElementTree as ET

SNDLIB_NS = {"snd": "http://sndlib.zib.de/network"}

DEFAULT_FIBER_TYPE = "G.652"
DEFAULT_ATTN_DB = 0.22
DEFAULT_MAX_SPAN_LENGTH_KM = 80.0

EARTH_RADIUS_KM = 6371.0


class SndlibTopologyParser:
    """Parses a native SNDlib network XML into nodes/edges dictionaries."""

    def __init__(self, maxSpanLengthKm=DEFAULT_MAX_SPAN_LENGTH_KM,
                 fiberType=DEFAULT_FIBER_TYPE, attnDb=DEFAULT_ATTN_DB):
        self.maxSpanLengthKm = maxSpanLengthKm
        self.fiberType = fiberType
        self.attnDb = attnDb

    def parse(self, xmlFile):
        """Parses the given SNDlib XML file.

        :param xmlFile: path to the SNDlib network XML file
        :return: tuple (nodes, edges) in the format of Nodes/Links_Germany_17.json
        """
        tree = ET.parse(xmlFile)
        root = tree.getroot()
        nodeCoords = self._parseNodes(root)
        edges = self._parseLinks(root, nodeCoords)
        nodes = {}
        for idx, nodeId in enumerate(sorted(nodeCoords)):
            lon, lat = nodeCoords[nodeId]
            nodes[str(idx)] = [nodeId, lon, lat, 0, 0]
        return nodes, edges

    def _parseNodes(self, root):
        nodeCoords = {}
        for node in root.findall(".//snd:nodes/snd:node", SNDLIB_NS):
            nodeId = node.get("id")
            x = float(node.findtext("snd:coordinates/snd:x", namespaces=SNDLIB_NS))
            y = float(node.findtext("snd:coordinates/snd:y", namespaces=SNDLIB_NS))
            nodeCoords[nodeId] = (x, y)
        if not nodeCoords:
            raise ValueError("No nodes found - not a valid SNDlib network file?")
        return nodeCoords

    def _parseLinks(self, root, nodeCoords):
        edges = {}
        linkNo = 0
        for link in root.findall(".//snd:links/snd:link", SNDLIB_NS):
            startNode = link.findtext("snd:source", namespaces=SNDLIB_NS)
            endNode = link.findtext("snd:target", namespaces=SNDLIB_NS)
            if startNode not in nodeCoords or endNode not in nodeCoords:
                raise ValueError(
                    f"Link {link.get('id')} references unknown node(s): "
                    f"{startNode}, {endNode}")
            linkDist = self._haversineKm(nodeCoords[startNode], nodeCoords[endNode])
            noSpans = max(1, math.ceil(linkDist / self.maxSpanLengthKm))
            spanList = self._buildSpanList(startNode, endNode, linkDist, noSpans)
            edges[str(linkNo)] = {
                "linkNo": linkNo,
                "startNode": startNode,
                "endNode": endNode,
                "linkDist": linkDist,
                "noChannels": 0,
                "noSpans": noSpans,
                "spanList": spanList,
            }
            linkNo += 1
        if not edges:
            raise ValueError("No links found - not a valid SNDlib network file?")
        return edges

    def _buildSpanList(self, startNode, endNode, linkDist, noSpans):
        spanList = []
        spanLength = linkDist / noSpans
        for span in range(1, noSpans + 1):
            spanList.append({
                "LinkName": f"Span_{startNode}_{endNode}_{span}",
                "FiberType": self.fiberType,
                "SpanLength": spanLength,
                "EDFAGain": spanLength * self.attnDb,
                "attnDB": self.attnDb,
            })
        return spanList

    @staticmethod
    def _haversineKm(coordA, coordB):
        """Great-circle distance in km between two (lon, lat) coordinates."""
        lon1, lat1 = map(math.radians, coordA)
        lon2, lat2 = map(math.radians, coordB)
        dLon = lon2 - lon1
        dLat = lat2 - lat1
        a = math.sin(dLat / 2) ** 2 + math.cos(lat1) * math.cos(lat2) * math.sin(dLon / 2) ** 2
        return EARTH_RADIUS_KM * 2 * math.asin(math.sqrt(a))
